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

@Service
public class KnowledgePackageService {

    private final KnowledgePackageRepository knowledgePackageRepository;

    public KnowledgePackageService(KnowledgePackageRepository knowledgePackageRepository) {
        this.knowledgePackageRepository = knowledgePackageRepository;
    }

    public PageResult<KnowledgePackage> page(KnowledgePackagePageQuery query) {
        return knowledgePackageRepository.page(query);
    }

    public KnowledgePackage detail(Long id) {
        return knowledgePackageRepository.findById(id);
    }

    public List<KnowledgePackageItem> listItems(Long packageId) {
        return knowledgePackageRepository.listItems(packageId);
    }

    public KnowledgePackage create(KnowledgePackageSaveRequest request) {
        return knowledgePackageRepository.create(request);
    }

    public KnowledgePackage update(Long id, KnowledgePackageSaveRequest request) {
        return knowledgePackageRepository.update(id, request);
    }

    public KnowledgePackageItem addItem(Long packageId, KnowledgePackageItemSaveRequest request) {
        return knowledgePackageRepository.addItem(packageId, request);
    }

    public void deleteItem(Long packageId, Long itemId) {
        knowledgePackageRepository.deleteItem(packageId, itemId);
    }
}
