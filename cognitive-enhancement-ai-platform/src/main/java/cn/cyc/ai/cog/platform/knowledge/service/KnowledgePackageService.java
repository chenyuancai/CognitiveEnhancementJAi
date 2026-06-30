package cn.cyc.ai.cog.platform.knowledge.service;

import cn.cyc.ai.cog.common.page.PageResult;
import cn.cyc.ai.cog.platform.knowledge.domain.KnowledgePackage;
import cn.cyc.ai.cog.platform.knowledge.domain.KnowledgePackageItem;
import cn.cyc.ai.cog.platform.knowledge.dto.KnowledgePackageItemSaveRequest;
import cn.cyc.ai.cog.platform.knowledge.dto.KnowledgePackagePageQuery;
import cn.cyc.ai.cog.platform.knowledge.dto.KnowledgePackageSaveRequest;
import cn.cyc.ai.cog.platform.knowledge.repository.KnowledgePackageRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 知识Package服务
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Service
public class KnowledgePackageService {

    /** 知识Package仓储。 */
    private final KnowledgePackageRepository knowledgePackageRepository;

    /**
     * 创建知识Package服务。
     *
     * @param knowledgePackageRepository 知识Package仓储
     */
    public KnowledgePackageService(KnowledgePackageRepository knowledgePackageRepository) {
        this.knowledgePackageRepository = knowledgePackageRepository;
    }

    /**
     * 执行分页。
     *
     * @param query 查询
     * @return 执行结果
     */
    public PageResult<KnowledgePackage> page(KnowledgePackagePageQuery query) {
        return knowledgePackageRepository.page(query);
    }

    /**
     * 执行detail。
     *
     * @param id 主键 ID
     * @return 执行结果
     */
    public KnowledgePackage detail(Long id) {
        return knowledgePackageRepository.findById(id);
    }

    /**
     * 查询Items列表。
     *
     * @param packageId packageID
     * @return 结果列表
     */
    public List<KnowledgePackageItem> listItems(Long packageId) {
        return knowledgePackageRepository.listItems(packageId);
    }

    /**
     * 创建Item。
     *
     * @param request 请求
     * @return 创建结果
     */
    public KnowledgePackage create(KnowledgePackageSaveRequest request) {
        return knowledgePackageRepository.create(request);
    }

    /**
     * 更新Item。
     *
     * @param id 主键 ID
     * @param request 请求
     * @return 更新结果
     */
    public KnowledgePackage update(Long id, KnowledgePackageSaveRequest request) {
        return knowledgePackageRepository.update(id, request);
    }

    /**
     * 执行addItem。
     *
     * @param packageId packageID
     * @param request 请求
     * @return 执行结果
     */
    public KnowledgePackageItem addItem(Long packageId, KnowledgePackageItemSaveRequest request) {
        return knowledgePackageRepository.addItem(packageId, request);
    }

    /**
     * 删除Item。
     *
     * @param packageId packageID
     * @param itemId itemID
     */
    public void deleteItem(Long packageId, Long itemId) {
        knowledgePackageRepository.deleteItem(packageId, itemId);
    }
}
