package cn.cyc.ai.cog.center.support;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import cn.cyc.ai.cog.core.metadata.MetadataDefinition;
import cn.cyc.ai.cog.core.metadata.MetadataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 基于 JSON 文件的元数据仓储基类，用于在无外部数据库时保留后台配置。
 *
 * @param <T> 元数据定义类型
 * @author cyc
 */
public abstract class AbstractJsonFileMetadataRepository<T extends MetadataDefinition> implements MetadataRepository<T> {

    private static final Logger log = LoggerFactory.getLogger(AbstractJsonFileMetadataRepository.class);

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .findAndRegisterModules()
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private final Path storageFile;

    private final Class<T> definitionType;

    private final Map<String, T> storage = new LinkedHashMap<>();

    protected AbstractJsonFileMetadataRepository(Path storageFile, Class<T> definitionType) {
        this.storageFile = storageFile;
        this.definitionType = definitionType;
        loadFromFile();
    }

    @Override
    public synchronized Optional<T> findByCode(String code) {
        return Optional.ofNullable(storage.get(code));
    }

    @Override
    public synchronized List<T> listAll() {
        return storage.values().stream()
                .sorted((left, right) -> left.code().compareTo(right.code()))
                .toList();
    }

    @Override
    public synchronized T save(T definition) {
        storage.put(definition.code(), definition);
        writeToFile();
        log.info("持久化元数据定义，repository={}, code={}, file={}",
                getClass().getSimpleName(), definition.code(), storageFile);
        return definition;
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
                    .constructCollectionType(List.class, definitionType);
            List<T> definitions = OBJECT_MAPPER.readValue(storageFile.toFile(), listType);
            for (T definition : definitions) {
                storage.put(definition.code(), definition);
            }
            log.info("加载持久化元数据定义，repository={}, size={}, file={}",
                    getClass().getSimpleName(), storage.size(), storageFile);
        } catch (IOException ex) {
            throw new IllegalStateException("加载元数据持久化文件失败: " + storageFile, ex);
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
                    .writeValue(tempFile.toFile(), new ArrayList<>(storage.values()));
            Files.move(tempFile, storageFile, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            throw new IllegalStateException("写入元数据持久化文件失败: " + storageFile, ex);
        }
    }
}
