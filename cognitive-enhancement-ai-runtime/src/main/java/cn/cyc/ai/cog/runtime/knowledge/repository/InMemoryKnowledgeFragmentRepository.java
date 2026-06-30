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
 * @date 2026/6/15 14:18
 */
@Repository
@ConditionalOnProperty(name = "cog.persistence.enabled", havingValue = "false", matchIfMissing = true)
public class InMemoryKnowledgeFragmentRepository implements KnowledgeFragmentRepository {

    /**
     * 内存知识片段容器。
     */
    private final CopyOnWriteArrayList<KnowledgeFragment> fragments = new CopyOnWriteArrayList<>();

    /**
     * 执行save。
     *
     * @param fragment fragment
     */
    @Override
    public void save(KnowledgeFragment fragment) {
        fragments.add(fragment);
    }

    /**
     * 查找人FragmentID。
     *
     * @param fragmentId fragmentID
     * @return 查找结果
     */
    @Override
    public Optional<KnowledgeFragment> findByFragmentId(String fragmentId) {
        String tenantCode = TenantContext.currentTenantCode();
        return fragments.stream()
                .filter(fragment -> tenantCode.equals(fragment.tenantCode()))
                .filter(fragment -> fragmentId.equals(fragment.fragmentId()))
                .findFirst();
    }

    /**
     * 查找人知识编码。
     *
     * @param knowledgeCode 知识编码
     * @return 查找结果
     */
    @Override
    public List<KnowledgeFragment> findByKnowledgeCode(String knowledgeCode) {
        String tenantCode = TenantContext.currentTenantCode();
        return fragments.stream()
                .filter(fragment -> tenantCode.equals(fragment.tenantCode()))
                .filter(fragment -> knowledgeCode.equals(fragment.knowledgeCode()))
                .sorted(Comparator.comparing(KnowledgeFragment::recordedAt).reversed())
                .collect(Collectors.toList());
    }

    /**
     * 查询All列表。
     * @return 结果列表
     */
    @Override
    public List<KnowledgeFragment> listAll() {
        String tenantCode = TenantContext.currentTenantCode();
        return fragments.stream()
                .filter(fragment -> tenantCode.equals(fragment.tenantCode()))
                .sorted(Comparator.comparing(KnowledgeFragment::recordedAt).reversed())
                .collect(Collectors.toList());
    }
}
