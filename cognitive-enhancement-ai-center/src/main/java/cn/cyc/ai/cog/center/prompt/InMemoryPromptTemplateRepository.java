package cn.cyc.ai.cog.center.prompt;

import cn.cyc.ai.cog.core.metadata.prompt.PromptLifecycleStatus;
import cn.cyc.ai.cog.core.metadata.prompt.PromptTemplate;
import cn.cyc.ai.cog.core.metadata.prompt.PromptTemplateRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Prompt 模板内存仓储实现。
 *
 * @author cyc
 */
@Repository
@ConditionalOnProperty(name = "cog.persistence.enabled", havingValue = "false")
public class InMemoryPromptTemplateRepository implements PromptTemplateRepository {

    private final ConcurrentMap<String, PromptTemplate> storage = new ConcurrentHashMap<>();

    @Override
    public Optional<PromptTemplate> findByCode(String code) {
        return findPublishedByPromptCode(code);
    }

    @Override
    public List<PromptTemplate> listAll() {
        return storage.values().stream()
                .sorted(Comparator.comparing(PromptTemplate::promptCode)
                        .thenComparing(PromptTemplate::version))
                .toList();
    }

    @Override
    public List<PromptTemplate> listVersionsByPromptCode(String promptCode) {
        return storage.values().stream()
                .filter(item -> item.promptCode().equals(promptCode))
                .sorted(Comparator.comparing(PromptTemplate::version))
                .toList();
    }

    @Override
    public PromptTemplate save(PromptTemplate definition) {
        storage.put(versionKey(definition.promptCode(), definition.version()), definition);
        return definition;
    }

    private String versionKey(String promptCode, String version) {
        return promptCode + "@" + version;
    }
}
