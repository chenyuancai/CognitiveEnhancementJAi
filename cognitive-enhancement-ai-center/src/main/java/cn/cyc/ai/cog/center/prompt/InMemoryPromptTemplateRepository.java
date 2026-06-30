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
 * @date 2026/6/15 14:18
 */
@Repository
@ConditionalOnProperty(name = "cog.persistence.enabled", havingValue = "false")
public class InMemoryPromptTemplateRepository implements PromptTemplateRepository {

    private final ConcurrentMap<String, PromptTemplate> storage = new ConcurrentHashMap<>();

    /**
     * 查找人编码。
     *
     * @param code 编码
     * @return 查找结果
     */
    @Override
    public Optional<PromptTemplate> findByCode(String code) {
        return findPublishedByPromptCode(code);
    }

    /**
     * 查询All列表。
     * @return 结果列表
     */
    @Override
    public List<PromptTemplate> listAll() {
        return storage.values().stream()
                .sorted(Comparator.comparing(PromptTemplate::promptCode)
                        .thenComparing(PromptTemplate::version))
                .toList();
    }

    /**
     * 查询Versions人提示词编码列表。
     *
     * @param promptCode 提示词编码
     * @return 结果列表
     */
    @Override
    public List<PromptTemplate> listVersionsByPromptCode(String promptCode) {
        return storage.values().stream()
                .filter(item -> item.promptCode().equals(promptCode))
                .sorted(Comparator.comparing(PromptTemplate::version))
                .toList();
    }

    /**
     * 执行save。
     *
     * @param definition definition
     * @return 执行结果
     */
    @Override
    public PromptTemplate save(PromptTemplate definition) {
        storage.put(versionKey(definition.promptCode(), definition.version()), definition);
        return definition;
    }

    /**
     * 执行版本号键。
     *
     * @param promptCode 提示词编码
     * @param version 版本号
     * @return 执行结果
     */
    private String versionKey(String promptCode, String version) {
        return promptCode + "@" + version;
    }
}
