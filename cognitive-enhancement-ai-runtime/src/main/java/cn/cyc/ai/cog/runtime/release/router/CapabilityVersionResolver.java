package cn.cyc.ai.cog.runtime.release.router;

import cn.cyc.ai.cog.core.exception.BusinessException;
import cn.cyc.ai.cog.core.metadata.capability.CapabilityDefinition;
import cn.cyc.ai.cog.core.metadata.capability.CapabilityDefinitionRepository;
import cn.cyc.ai.cog.core.metadata.capability.CapabilityTenantBindingRepository;
import cn.cyc.ai.cog.core.metadata.type.CommonStatus;
import cn.cyc.ai.cog.runtime.security.TenantContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Capability 运行时版本解析器：租户启停校验 + 发布指针灰度选版。
 *
 * @author cyc
 */
@Component
public class CapabilityVersionResolver {

    private static final Logger log = LoggerFactory.getLogger(CapabilityVersionResolver.class);

    private final CapabilityDefinitionRepository capabilityDefinitionRepository;
    private final CapabilityReleaseRouter capabilityReleaseRouter;
    private final CapabilityTenantBindingRepository tenantBindingRepository;

    public CapabilityVersionResolver(CapabilityDefinitionRepository capabilityDefinitionRepository,
                                     CapabilityReleaseRouter capabilityReleaseRouter,
                                     CapabilityTenantBindingRepository tenantBindingRepository) {
        this.capabilityDefinitionRepository = capabilityDefinitionRepository;
        this.capabilityReleaseRouter = capabilityReleaseRouter;
        this.tenantBindingRepository = tenantBindingRepository;
    }

    /**
     * 解析运行时生效的 Capability 定义。
     *
     * @param capabilityCode 能力编码
     * @param traceId        链路 ID
     * @return 解析后的能力定义
     */
    public CapabilityDefinition resolve(String capabilityCode, String traceId) {
        assertTenantEnabled(capabilityCode);
        CapabilityDefinition published = capabilityDefinitionRepository.findPublishedByCapabilityCode(capabilityCode)
                .orElseThrow(() -> new BusinessException("NOT_FOUND", "未找到能力: " + capabilityCode));
        if (published.status() != CommonStatus.ENABLED) {
            throw new BusinessException("CONFLICT", "能力未启用: " + capabilityCode);
        }
        String resolvedVersion = capabilityReleaseRouter.resolveVersion(
                capabilityCode, traceId, published.version());
        CapabilityDefinition resolved = capabilityDefinitionRepository
                .findByCapabilityCodeAndVersion(capabilityCode, resolvedVersion)
                .orElse(published);
        log.debug("Capability 版本解析完成, capabilityCode={}, traceId={}, version={}",
                capabilityCode, traceId, resolved.version());
        return resolved;
    }

    private void assertTenantEnabled(String capabilityCode) {
        tenantBindingRepository.findByTenantAndCapability(TenantContext.currentTenantCode(), capabilityCode)
                .filter(binding -> !binding.enabled())
                .ifPresent(binding -> {
                    throw new BusinessException("CAPABILITY_DISABLED",
                            "租户未启用能力: " + capabilityCode);
                });
    }
}
