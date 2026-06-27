package cn.cyc.ai.cog.admin.ai.service;

import cn.cyc.ai.cog.admin.ai.dto.AiRoutingOverviewResult;
import cn.cyc.ai.cog.admin.ai.dto.CapabilityRoutingItem;
import cn.cyc.ai.cog.core.metadata.capability.CapabilityDefinition;
import cn.cyc.ai.cog.core.metadata.capability.CapabilityDefinitionRepository;
import cn.cyc.ai.cog.core.metadata.capability.CapabilityReleasePointer;
import cn.cyc.ai.cog.core.metadata.capability.CapabilityReleasePointerRepository;
import cn.cyc.ai.cog.core.metadata.capability.CapabilityTenantBindingRepository;
import cn.cyc.ai.cog.runtime.model.governance.DefaultModelGovernance;
import cn.cyc.ai.cog.runtime.security.TenantContext;
import cn.cyc.ai.cog.runtime.service.ModelRuntimeQueryService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * AI 路由与模型治理只读聚合。
 */
@Service
public class AiRoutingOverviewService {

    private final ModelRuntimeQueryService modelRuntimeQueryService;
    private final DefaultModelGovernance modelGovernance;
    private final CapabilityDefinitionRepository capabilityDefinitionRepository;
    private final CapabilityReleasePointerRepository releasePointerRepository;
    private final CapabilityTenantBindingRepository tenantBindingRepository;

    public AiRoutingOverviewService(ModelRuntimeQueryService modelRuntimeQueryService,
                                    DefaultModelGovernance modelGovernance,
                                    CapabilityDefinitionRepository capabilityDefinitionRepository,
                                    CapabilityReleasePointerRepository releasePointerRepository,
                                    CapabilityTenantBindingRepository tenantBindingRepository) {
        this.modelRuntimeQueryService = modelRuntimeQueryService;
        this.modelGovernance = modelGovernance;
        this.capabilityDefinitionRepository = capabilityDefinitionRepository;
        this.releasePointerRepository = releasePointerRepository;
        this.tenantBindingRepository = tenantBindingRepository;
    }

    public AiRoutingOverviewResult build() {
        AiRoutingOverviewResult result = new AiRoutingOverviewResult();
        result.setModelOverview(modelRuntimeQueryService.getModelStatusOverview());
        result.setGovernanceStates(modelGovernance.listGovernanceStates());
        result.setCapabilityRoutes(buildCapabilityRoutes());
        return result;
    }

    private List<CapabilityRoutingItem> buildCapabilityRoutes() {
        Map<String, CapabilityRoutingItem> grouped = new LinkedHashMap<>();
        for (CapabilityDefinition definition : capabilityDefinitionRepository.listAll()) {
            grouped.computeIfAbsent(definition.capabilityCode(), code -> {
                CapabilityRoutingItem item = new CapabilityRoutingItem();
                item.setCapabilityCode(code);
                item.setTenantEnabled(true);
                capabilityDefinitionRepository.findPublishedByCapabilityCode(code)
                        .ifPresent(published -> item.setPublishedVersion(published.version()));
                releasePointerRepository.findByCapabilityCode(code).ifPresent(pointer -> {
                    item.setBaselineVersion(pointer.baselineVersion());
                    item.setCandidateVersion(pointer.candidateVersion());
                    item.setGrayEnabled(StringUtils.hasText(pointer.candidateVersion()));
                });
                String tenantCode = TenantContext.currentTenantCode();
                if (StringUtils.hasText(tenantCode)) {
                    tenantBindingRepository.findByTenantAndCapability(tenantCode, code)
                            .ifPresent(binding -> item.setTenantEnabled(binding.enabled()));
                }
                return item;
            });
        }
        return grouped.values().stream().toList();
    }
}
