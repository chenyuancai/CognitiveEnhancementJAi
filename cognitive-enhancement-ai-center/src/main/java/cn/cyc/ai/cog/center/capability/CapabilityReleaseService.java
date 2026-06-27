package cn.cyc.ai.cog.center.capability;

import cn.cyc.ai.cog.common.exception.Errors;
import cn.cyc.ai.cog.common.exception.PlatformErrorCode;
import cn.cyc.ai.cog.core.metadata.capability.CapabilityDefinition;
import cn.cyc.ai.cog.core.metadata.capability.CapabilityDefinitionRepository;
import cn.cyc.ai.cog.core.metadata.capability.CapabilityLifecycleStatus;
import cn.cyc.ai.cog.core.metadata.capability.CapabilityReleasePointer;
import cn.cyc.ai.cog.core.metadata.capability.CapabilityReleasePointerRepository;
import cn.cyc.ai.cog.core.metadata.capability.CapabilityTenantBinding;
import cn.cyc.ai.cog.core.metadata.capability.CapabilityTenantBindingRepository;
import cn.cyc.ai.cog.core.metadata.prompt.PromptGrayRule;
import cn.cyc.ai.cog.core.metadata.type.CommonStatus;
import cn.cyc.ai.cog.runtime.security.TenantContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Capability 发布、灰度与租户启停管理服务。
 *
 * @author cyc
 */
@Service
public class CapabilityReleaseService {

    private static final Logger log = LoggerFactory.getLogger(CapabilityReleaseService.class);

    private final CapabilityDefinitionRepository capabilityDefinitionRepository;
    private final CapabilityReleasePointerRepository releasePointerRepository;
    private final CapabilityTenantBindingRepository tenantBindingRepository;

    public CapabilityReleaseService(CapabilityDefinitionRepository capabilityDefinitionRepository,
                                    CapabilityReleasePointerRepository releasePointerRepository,
                                    CapabilityTenantBindingRepository tenantBindingRepository) {
        this.capabilityDefinitionRepository = capabilityDefinitionRepository;
        this.releasePointerRepository = releasePointerRepository;
        this.tenantBindingRepository = tenantBindingRepository;
    }

    public List<CapabilityResult> listVersions(String capabilityCode) {
        ensureAnyVersionExists(capabilityCode);
        return capabilityDefinitionRepository.listVersionsByCapabilityCode(capabilityCode).stream()
                .map(this::toResult)
                .toList();
    }

    public CapabilityResult createDraft(String capabilityCode, CapabilityDraftRequest request) {
        CapabilityDefinition base = resolveBaseDefinition(capabilityCode, request);
        String version = resolveDraftVersion(capabilityCode, request == null ? null : request.version());
        CapabilityDefinition draft = new CapabilityDefinition(
                capabilityCode,
                request != null && StringUtils.hasText(request.capabilityName())
                        ? request.capabilityName()
                        : base.capabilityName(),
                request != null && StringUtils.hasText(request.capabilityDesc())
                        ? request.capabilityDesc()
                        : base.capabilityDesc(),
                request != null && request.inputSchema() != null ? request.inputSchema() : base.inputSchema(),
                request != null && request.outputSchema() != null ? request.outputSchema() : base.outputSchema(),
                request != null && request.parameterConstraints() != null
                        ? request.parameterConstraints()
                        : base.parameterConstraints(),
                request != null && request.executeMode() != null ? request.executeMode() : base.executeMode(),
                request != null && StringUtils.hasText(request.boundAgentCode())
                        ? request.boundAgentCode()
                        : base.boundAgentCode(),
                request != null && request.riskLevel() != null ? request.riskLevel() : base.riskLevel(),
                request != null && request.needHumanConfirm() != null
                        ? request.needHumanConfirm()
                        : base.needHumanConfirm(),
                CommonStatus.ENABLED,
                version,
                null,
                CapabilityLifecycleStatus.DRAFT
        );
        capabilityDefinitionRepository.save(draft);
        log.info("创建 Capability 草稿, capabilityCode={}, version={}", capabilityCode, version);
        return toResult(draft);
    }

    public CapabilityResult publish(String capabilityCode, CapabilityPublishRequest request) {
        String version = Objects.requireNonNull(request.version(), "version 不能为空");
        CapabilityDefinition target = findVersion(capabilityCode, version);
        if (target.lifecycleStatus() == CapabilityLifecycleStatus.PUBLISHED) {
            return toResult(target);
        }
        capabilityDefinitionRepository.listVersionsByCapabilityCode(capabilityCode).stream()
                .filter(item -> item.lifecycleStatus() == CapabilityLifecycleStatus.PUBLISHED)
                .forEach(item -> capabilityDefinitionRepository.save(offlineCopy(item)));
        Instant now = Instant.now();
        CapabilityDefinition published = new CapabilityDefinition(
                target.capabilityCode(),
                target.capabilityName(),
                target.capabilityDesc(),
                target.inputSchema(),
                target.outputSchema(),
                target.parameterConstraints(),
                target.executeMode(),
                target.boundAgentCode(),
                target.riskLevel(),
                target.needHumanConfirm(),
                target.status(),
                target.version(),
                now,
                CapabilityLifecycleStatus.PUBLISHED
        );
        capabilityDefinitionRepository.save(published);
        releasePointerRepository.save(new CapabilityReleasePointer(
                TenantContext.currentTenantCode(),
                capabilityCode,
                version,
                null,
                null
        ));
        log.info("发布 Capability 版本, capabilityCode={}, version={}", capabilityCode, version);
        return toResult(published);
    }

    public CapabilityReleasePointer configureGray(String capabilityCode, CapabilityGrayRequest request) {
        PromptGrayRule grayRule = Objects.requireNonNull(request.grayRule(), "grayRule 不能为空");
        findVersion(capabilityCode, grayRule.baselineVersion());
        findVersion(capabilityCode, grayRule.candidateVersion());
        CapabilityReleasePointer pointer = new CapabilityReleasePointer(
                TenantContext.currentTenantCode(),
                capabilityCode,
                grayRule.baselineVersion(),
                grayRule.candidateVersion(),
                grayRule
        );
        releasePointerRepository.save(pointer);
        log.info("配置 Capability 灰度, capabilityCode={}, baseline={}, candidate={}, percentage={}",
                capabilityCode, grayRule.baselineVersion(), grayRule.candidateVersion(), grayRule.percentage());
        return pointer;
    }

    public CapabilityTenantBinding configureTenant(String capabilityCode,
                                                   String tenantCode,
                                                   CapabilityTenantBindingRequest request) {
        ensureAnyVersionExists(capabilityCode);
        CapabilityTenantBinding binding = new CapabilityTenantBinding(
                TenantContext.normalize(tenantCode),
                capabilityCode,
                request.enabled()
        );
        tenantBindingRepository.save(binding);
        log.info("配置 Capability 租户启停, tenantCode={}, capabilityCode={}, enabled={}",
                tenantCode, capabilityCode, request.enabled());
        return binding;
    }

    private CapabilityDefinition resolveBaseDefinition(String capabilityCode, CapabilityDraftRequest request) {
        List<CapabilityDefinition> versions = capabilityDefinitionRepository.listVersionsByCapabilityCode(capabilityCode);
        if (versions.isEmpty()) {
            throw Errors.of(PlatformErrorCode.METADATA_BASELINE_NOT_FOUND, "未找到 Capability 基线版本: " + capabilityCode);
        }
        return versions.stream()
                .max(Comparator.comparing(CapabilityDefinition::version))
                .orElseThrow();
    }

    private String resolveDraftVersion(String capabilityCode, String requestedVersion) {
        if (StringUtils.hasText(requestedVersion)) {
            capabilityDefinitionRepository.findByCapabilityCodeAndVersion(capabilityCode, requestedVersion)
                    .ifPresent(existing -> {
                        throw Errors.of(PlatformErrorCode.METADATA_VERSION_EXISTS, "版本已存在: " + requestedVersion);
                    });
            return requestedVersion;
        }
        String latest = capabilityDefinitionRepository.listVersionsByCapabilityCode(capabilityCode).stream()
                .map(CapabilityDefinition::version)
                .max(String::compareTo)
                .orElse("1.0.0");
        return bumpPatchVersion(latest);
    }

    private String bumpPatchVersion(String version) {
        String[] parts = version.split("\\.");
        if (parts.length >= 3) {
            try {
                int patch = Integer.parseInt(parts[2]);
                return parts[0] + "." + parts[1] + "." + (patch + 1);
            } catch (NumberFormatException ignored) {
                // fall through
            }
        }
        return version + ".1";
    }

    private CapabilityDefinition findVersion(String capabilityCode, String version) {
        return capabilityDefinitionRepository.findByCapabilityCodeAndVersion(capabilityCode, version)
                .orElseThrow(() -> Errors.of(PlatformErrorCode.RUNTIME_CAPABILITY_NOT_FOUND,
                        "未找到 Capability 版本: " + capabilityCode + "@" + version));
    }

    private void ensureAnyVersionExists(String capabilityCode) {
        if (capabilityDefinitionRepository.listVersionsByCapabilityCode(capabilityCode).isEmpty()) {
            throw Errors.of(PlatformErrorCode.RUNTIME_CAPABILITY_NOT_FOUND, "未找到 Capability: " + capabilityCode);
        }
    }

    private CapabilityDefinition offlineCopy(CapabilityDefinition definition) {
        return new CapabilityDefinition(
                definition.capabilityCode(),
                definition.capabilityName(),
                definition.capabilityDesc(),
                definition.inputSchema(),
                definition.outputSchema(),
                definition.parameterConstraints(),
                definition.executeMode(),
                definition.boundAgentCode(),
                definition.riskLevel(),
                definition.needHumanConfirm(),
                definition.status(),
                definition.version(),
                definition.publishedAt(),
                CapabilityLifecycleStatus.OFFLINE
        );
    }

    private CapabilityResult toResult(CapabilityDefinition definition) {
        return new CapabilityResult(
                definition.capabilityCode(),
                definition.capabilityName(),
                definition.capabilityDesc(),
                definition.inputSchema(),
                definition.outputSchema(),
                definition.parameterConstraints(),
                definition.executeMode(),
                definition.boundAgentCode(),
                definition.riskLevel(),
                definition.needHumanConfirm(),
                definition.status(),
                definition.version(),
                definition.lifecycleStatus(),
                definition.publishedAt()
        );
    }
}
