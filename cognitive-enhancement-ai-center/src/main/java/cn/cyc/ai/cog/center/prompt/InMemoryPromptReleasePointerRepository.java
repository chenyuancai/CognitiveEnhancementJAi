package cn.cyc.ai.cog.center.prompt;

import cn.cyc.ai.cog.core.metadata.prompt.PromptReleasePointer;
import cn.cyc.ai.cog.core.metadata.prompt.PromptReleasePointerRepository;
import cn.cyc.ai.cog.runtime.security.TenantContext;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Prompt 发布指针内存仓储。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Repository
@ConditionalOnProperty(name = "cog.persistence.enabled", havingValue = "false")
public class InMemoryPromptReleasePointerRepository implements PromptReleasePointerRepository {

    private final ConcurrentMap<String, PromptReleasePointer> storage = new ConcurrentHashMap<>();

    /**
     * 查找人提示词编码。
     *
     * @param promptCode 提示词编码
     * @return 查找结果
     */
    @Override
    public Optional<PromptReleasePointer> findByPromptCode(String promptCode) {
        return Optional.ofNullable(storage.get(key(promptCode)));
    }

    /**
     * 执行save。
     *
     * @param pointer pointer
     * @return 执行结果
     */
    @Override
    public PromptReleasePointer save(PromptReleasePointer pointer) {
        storage.put(key(pointer.promptCode()), pointer);
        return pointer;
    }

    /**
     * 执行键。
     *
     * @param promptCode 提示词编码
     * @return 执行结果
     */
    private String key(String promptCode) {
        return TenantContext.currentTenantCode() + ":" + promptCode;
    }
}
