package cn.cyc.ai.cog.runtime.knowledge.repository;

import cn.cyc.ai.cog.runtime.knowledge.domain.KnowledgeFragment;
import cn.cyc.ai.cog.runtime.knowledge.spi.KnowledgeFragmentRepository;
import cn.cyc.ai.cog.runtime.security.TenantContext;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * 内存知识片段仓储。
 *
 * @author cyc
 */
@Repository
@ConditionalOnProperty(name = "cog.persistence.enabled", havingValue = "false", matchIfMissing = true)
public class InMemoryKnowledgeFragmentRepository implements KnowledgeFragmentRepository {

    /**
     * 内存知识片段容器。
     */
    private final CopyOnWriteArrayList<KnowledgeFragment> fragments = new CopyOnWriteArrayList<>();

    @Override
    public void save(KnowledgeFragment fragment) {
        fragments.add(fragment);
    }

    @Override
    public Optional<KnowledgeFragment> findByFragmentId(String fragmentId) {
        String tenantCode = TenantContext.currentTenantCode();
        return fragments.stream()
                .filter(fragment -> tenantCode.equals(fragment.tenantCode()))
                .filter(fragment -> fragmentId.equals(fragment.fragmentId()))
                .findFirst();
    }

    @Override
    public List<KnowledgeFragment> findByKnowledgeCode(String knowledgeCode) {
        String tenantCode = TenantContext.currentTenantCode();
        return fragments.stream()
                .filter(fragment -> tenantCode.equals(fragment.tenantCode()))
                .filter(fragment -> knowledgeCode.equals(fragment.knowledgeCode()))
                .sorted(Comparator.comparing(KnowledgeFragment::recordedAt).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<KnowledgeFragment> listAll() {
        String tenantCode = TenantContext.currentTenantCode();
        return fragments.stream()
                .filter(fragment -> tenantCode.equals(fragment.tenantCode()))
                .sorted(Comparator.comparing(KnowledgeFragment::recordedAt).reversed())
                .collect(Collectors.toList());
    }
}
