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
 */
@Repository
@ConditionalOnProperty(name = "cog.persistence.enabled", havingValue = "false")
public class InMemoryPromptReleasePointerRepository implements PromptReleasePointerRepository {

    private final ConcurrentMap<String, PromptReleasePointer> storage = new ConcurrentHashMap<>();

    @Override
    public Optional<PromptReleasePointer> findByPromptCode(String promptCode) {
        return Optional.ofNullable(storage.get(key(promptCode)));
    }

    @Override
    public PromptReleasePointer save(PromptReleasePointer pointer) {
        storage.put(key(pointer.promptCode()), pointer);
        return pointer;
    }

    private String key(String promptCode) {
        return TenantContext.currentTenantCode() + ":" + promptCode;
    }
}
