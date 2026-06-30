package cn.cyc.ai.cog.platform.knowledge.repository;

import cn.cyc.ai.cog.common.page.PageResult;
import cn.cyc.ai.cog.platform.knowledge.domain.KnowledgePackage;
import cn.cyc.ai.cog.platform.knowledge.domain.KnowledgePackageItem;
import cn.cyc.ai.cog.platform.knowledge.dto.KnowledgePackageItemSaveRequest;
import cn.cyc.ai.cog.platform.knowledge.dto.KnowledgePackagePageQuery;
import cn.cyc.ai.cog.platform.knowledge.dto.KnowledgePackageSaveRequest;

import java.util.List;

/**
 * 知识Package仓储
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public interface KnowledgePackageRepository {

    PageResult<KnowledgePackage> page(KnowledgePackagePageQuery query);

    KnowledgePackage findById(Long id);

    List<KnowledgePackageItem> listItems(Long packageId);

    KnowledgePackage create(KnowledgePackageSaveRequest request);

    KnowledgePackage update(Long id, KnowledgePackageSaveRequest request);

    KnowledgePackageItem addItem(Long packageId, KnowledgePackageItemSaveRequest request);

    void deleteItem(Long packageId, Long itemId);

    /**
     * 查询已启用的知识包列表。
     *
     * @param tenantId 租户 ID，可为 null
     * @return 知识包列表
     */
    java.util.List<KnowledgePackage> listEnabled(Long tenantId);
}
