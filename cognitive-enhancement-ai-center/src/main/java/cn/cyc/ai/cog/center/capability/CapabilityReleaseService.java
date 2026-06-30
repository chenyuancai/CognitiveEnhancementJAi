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
 * @date 2026/6/15 14:18
 */
@Service
public class CapabilityReleaseService {

    /** 日志记录器 */
    private static final Logger log = LoggerFactory.getLogger(CapabilityReleaseService.class);

    /** 能力Definition仓储。 */
    private final CapabilityDefinitionRepository capabilityDefinitionRepository;
    /** releasePointer仓储。 */
    private final CapabilityReleasePointerRepository releasePointerRepository;
    /** 租户Binding仓储。 */
    private final CapabilityTenantBindingRepository tenantBindingRepository;

    /**
     * 创建能力Release服务。
     */
    public CapabilityReleaseService(CapabilityDefinitionRepository capabilityDefinitionRepository,
                                    CapabilityReleasePointerRepository releasePointerRepository,
                                    CapabilityTenantBindingRepository tenantBindingRepository) {
        this.capabilityDefinitionRepository = capabilityDefinitionRepository;
        this.releasePointerRepository = releasePointerRepository;
        this.tenantBindingRepository = tenantBindingRepository;
    }

    /**
     * 查询Versions列表。
     *
     * @param capabilityCode 能力编码
     * @return 结果列表
     */
    public List<CapabilityResult> listVersions(String capabilityCode) {
        ensureAnyVersionExists(capabilityCode);
        return capabilityDefinitionRepository.listVersionsByCapabilityCode(capabilityCode).stream()
                .map(this::toResult)
                .toList();
    }

    /**
     * 创建Draft。
     *
     * @param capabilityCode 能力编码
     * @param request 请求
     * @return 创建结果
     */
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

    /**
     * 执行publish。
     *
     * @param capabilityCode 能力编码
     * @param request 请求
     * @return 执行结果
     */
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

    /**
     * 执行configureGray。
     *
     * @param capabilityCode 能力编码
     * @param request 请求
     * @return 执行结果
     */
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

    /**
     * 执行configure租户。
     * @return 执行结果
     */
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

    /**
     * 执行resolveBaseDefinition。
     *
     * @param capabilityCode 能力编码
     * @param request 请求
     * @return 执行结果
     */
    private CapabilityDefinition resolveBaseDefinition(String capabilityCode, CapabilityDraftRequest request) {
        List<CapabilityDefinition> versions = capabilityDefinitionRepository.listVersionsByCapabilityCode(capabilityCode);
        if (versions.isEmpty()) {
            throw Errors.of(PlatformErrorCode.METADATA_BASELINE_NOT_FOUND, "未找到 Capability 基线版本: " + capabilityCode);
        }
        return versions.stream()
                .max(Comparator.comparing(CapabilityDefinition::version))
                .orElseThrow();
    }

    /**
     * 执行resolveDraft版本号。
     *
     * @param capabilityCode 能力编码
     * @param requestedVersion requested版本号
     * @return 执行结果
     */
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

    /**
     * 执行bumpPatch版本号。
     *
     * @param version 版本号
     * @return 执行结果
     */
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

    /**
     * 查找版本号。
     *
     * @param capabilityCode 能力编码
     * @param version 版本号
     * @return 查找结果
     */
    private CapabilityDefinition findVersion(String capabilityCode, String version) {
        return capabilityDefinitionRepository.findByCapabilityCodeAndVersion(capabilityCode, version)
                .orElseThrow(() -> Errors.of(PlatformErrorCode.RUNTIME_CAPABILITY_NOT_FOUND,
                        "未找到 Capability 版本: " + capabilityCode + "@" + version));
    }

    /**
     * 执行ensureAny版本号Exists。
     *
     * @param capabilityCode 能力编码
     */
    private void ensureAnyVersionExists(String capabilityCode) {
        if (capabilityDefinitionRepository.listVersionsByCapabilityCode(capabilityCode).isEmpty()) {
            throw Errors.of(PlatformErrorCode.RUNTIME_CAPABILITY_NOT_FOUND, "未找到 Capability: " + capabilityCode);
        }
    }

    /**
     * 执行offlineCopy。
     *
     * @param definition definition
     * @return 执行结果
     */
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

    /**
     * 转换为结果。
     *
     * @param definition definition
     * @return 转换结果
     */
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
