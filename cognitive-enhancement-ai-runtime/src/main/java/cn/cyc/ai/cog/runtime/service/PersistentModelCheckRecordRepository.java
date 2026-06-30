package cn.cyc.ai.cog.runtime.service;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import cn.cyc.ai.cog.runtime.domain.ModelCheckRecord;
import cn.cyc.ai.cog.runtime.spi.ModelCheckRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 基于 JSON 文件的模型检查记录仓储。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Component
@ConditionalOnProperty(name = "cog.persistence.enabled", havingValue = "true")
public class PersistentModelCheckRecordRepository implements ModelCheckRecordRepository {

    /** 日志记录器 */
    private static final Logger log = LoggerFactory.getLogger(PersistentModelCheckRecordRepository.class);

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .findAndRegisterModules()
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    /** storage文件。 */
    private final Path storageFile;

    /** records。 */
    private final CopyOnWriteArrayList<ModelCheckRecord> records = new CopyOnWriteArrayList<>();

    /**
     * 创建Persistent模型CheckRecord仓储。
     *
     * @param persistenceDir persistenceDir
     */
    @Autowired
    public PersistentModelCheckRecordRepository(@Value("${cog.persistence.dir:data/cognitive-enhancement-ai}") String persistenceDir) {
        this(Path.of(persistenceDir));
    }

    /**
     * 创建Persistent模型CheckRecord仓储。
     *
     * @param persistenceDir persistenceDir
     */
    public PersistentModelCheckRecordRepository(Path persistenceDir) {
        this.storageFile = persistenceDir.resolve("runtime-model-check-records.json");
        loadFromFile();
    }

    /**
     * 执行save。
     *
     * @param record record
     */
    @Override
    public void save(ModelCheckRecord record) {
        records.add(0, record);
        writeToFile();
        log.debug("持久化模型检查记录, traceId={}, providerCode={}, modelCode={}, file={}",
                record.traceId(), record.providerCode(), record.modelCode(), storageFile);
    }

    /**
     * 查询All列表。
     * @return 结果列表
     */
    @Override
    public List<ModelCheckRecord> listAll() {
        return new ArrayList<>(records);
    }

    /**
     * 执行loadFrom文件。
     */
    private void loadFromFile() {
        if (!Files.exists(storageFile)) {
            return;
        }
        try {
            if (Files.size(storageFile) == 0) {
                return;
            }
            JavaType listType = OBJECT_MAPPER.getTypeFactory()
                    .constructCollectionType(List.class, ModelCheckRecord.class);
            List<ModelCheckRecord> restoredRecords = OBJECT_MAPPER.readValue(storageFile.toFile(), listType);
            records.addAll(restoredRecords);
            log.info("加载模型检查持久化记录，size={}, file={}", records.size(), storageFile);
        } catch (IOException ex) {
            throw new IllegalStateException("加载模型检查持久化文件失败: " + storageFile, ex);
        }
    }

    /**
     * 执行writeTo文件。
     */
    private void writeToFile() {
        try {
            Path parent = storageFile.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Path tempFile = Files.createTempFile(parent, storageFile.getFileName().toString(), ".tmp");
            OBJECT_MAPPER.writerWithDefaultPrettyPrinter()
                    .writeValue(tempFile.toFile(), new ArrayList<>(records));
            Files.move(tempFile, storageFile, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            throw new IllegalStateException("写入模型检查持久化文件失败: " + storageFile, ex);
        }
    }
}
