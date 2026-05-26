package cn.cyc.ai.cog.runtime.service;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import cn.cyc.ai.cog.runtime.domain.ModelCheckRecord;
import cn.cyc.ai.cog.runtime.spi.ModelCheckRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 */
@Component
@ConditionalOnProperty(name = "cog.persistence.enabled", havingValue = "true")
public class PersistentModelCheckRecordRepository implements ModelCheckRecordRepository {

    private static final Logger log = LoggerFactory.getLogger(PersistentModelCheckRecordRepository.class);

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .findAndRegisterModules()
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private final Path storageFile;

    private final CopyOnWriteArrayList<ModelCheckRecord> records = new CopyOnWriteArrayList<>();

    public PersistentModelCheckRecordRepository(@Value("${cog.persistence.dir:data/cognitive-enhancement-ai}") String persistenceDir) {
        this(Path.of(persistenceDir));
    }

    public PersistentModelCheckRecordRepository(Path persistenceDir) {
        this.storageFile = persistenceDir.resolve("runtime-model-check-records.json");
        loadFromFile();
    }

    @Override
    public void save(ModelCheckRecord record) {
        records.add(0, record);
        writeToFile();
        log.debug("持久化模型检查记录, traceId={}, providerCode={}, modelCode={}, file={}",
                record.traceId(), record.providerCode(), record.modelCode(), storageFile);
    }

    @Override
    public List<ModelCheckRecord> listAll() {
        return new ArrayList<>(records);
    }

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
