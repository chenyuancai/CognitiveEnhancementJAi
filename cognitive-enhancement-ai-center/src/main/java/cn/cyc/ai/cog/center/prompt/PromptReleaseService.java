package cn.cyc.ai.cog.center.prompt;

import cn.cyc.ai.cog.common.exception.Errors;
import cn.cyc.ai.cog.common.exception.PlatformErrorCode;
import cn.cyc.ai.cog.core.metadata.prompt.PromptGrayRule;
import cn.cyc.ai.cog.core.metadata.prompt.PromptLifecycleStatus;
import cn.cyc.ai.cog.core.metadata.prompt.PromptReleasePointer;
import cn.cyc.ai.cog.core.metadata.prompt.PromptReleasePointerRepository;
import cn.cyc.ai.cog.core.metadata.prompt.PromptTemplate;
import cn.cyc.ai.cog.core.metadata.prompt.PromptTemplateRepository;
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
 * Prompt 发布与灰度管理服务。
 *
 * @author cyc
 */
@Service
public class PromptReleaseService {

    private static final Logger log = LoggerFactory.getLogger(PromptReleaseService.class);

    private final PromptTemplateRepository promptTemplateRepository;
    private final PromptReleasePointerRepository releasePointerRepository;

    public PromptReleaseService(PromptTemplateRepository promptTemplateRepository,
                                PromptReleasePointerRepository releasePointerRepository) {
        this.promptTemplateRepository = promptTemplateRepository;
        this.releasePointerRepository = releasePointerRepository;
    }

    /**
     * 列出 Prompt 全部版本。
     *
     * @param promptCode Prompt 编码
     * @return 版本列表
     */
    public List<PromptResult> listVersions(String promptCode) {
        ensureAnyVersionExists(promptCode);
        return promptTemplateRepository.listVersionsByPromptCode(promptCode).stream()
                .map(this::toResult)
                .toList();
    }

    /**
     * 创建草稿新版本。
     *
     * @param promptCode Prompt 编码
     * @param request    草稿请求
     * @return 草稿版本
     */
    public PromptResult createDraft(String promptCode, PromptDraftRequest request) {
        PromptTemplate base = resolveBaseTemplate(promptCode, request);
        String version = resolveDraftVersion(promptCode, request == null ? null : request.version());
        PromptTemplate draft = new PromptTemplate(
                promptCode,
                request != null && StringUtils.hasText(request.promptName())
                        ? request.promptName()
                        : base.promptName(),
                request != null && StringUtils.hasText(request.scenarioCode())
                        ? request.scenarioCode()
                        : base.scenarioCode(),
                version,
                request != null && StringUtils.hasText(request.templateContent())
                        ? request.templateContent()
                        : base.templateContent(),
                request != null && request.variableSchema() != null
                        ? request.variableSchema()
                        : base.variableSchema(),
                request != null && request.outputSchema() != null
                        ? request.outputSchema()
                        : base.outputSchema(),
                CommonStatus.ENABLED,
                null,
                PromptLifecycleStatus.DRAFT
        );
        promptTemplateRepository.save(draft);
        log.info("创建 Prompt 草稿, promptCode={}, version={}", promptCode, version);
        return toResult(draft);
    }

    /**
     * 发布指定版本：同 promptCode 其他 PUBLISHED 自动下线。
     *
     * @param promptCode Prompt 编码
     * @param request    发布请求
     * @return 发布后版本
     */
    public PromptResult publish(String promptCode, PromptPublishRequest request) {
        String version = Objects.requireNonNull(request.version(), "version 不能为空");
        PromptTemplate target = findVersion(promptCode, version);
        if (target.lifecycleStatus() == PromptLifecycleStatus.PUBLISHED) {
            return toResult(target);
        }
        promptTemplateRepository.listVersionsByPromptCode(promptCode).stream()
                .filter(item -> item.lifecycleStatus() == PromptLifecycleStatus.PUBLISHED)
                .forEach(item -> promptTemplateRepository.save(offlineCopy(item)));
        Instant now = Instant.now();
        PromptTemplate published = new PromptTemplate(
                target.promptCode(),
                target.promptName(),
                target.scenarioCode(),
                target.version(),
                target.templateContent(),
                target.variableSchema(),
                target.outputSchema(),
                target.status(),
                now,
                PromptLifecycleStatus.PUBLISHED
        );
        promptTemplateRepository.save(published);
        releasePointerRepository.save(new PromptReleasePointer(
                TenantContext.currentTenantCode(),
                promptCode,
                version,
                null,
                null
        ));
        log.info("发布 Prompt 版本, promptCode={}, version={}", promptCode, version);
        return toResult(published);
    }

    /**
     * 下线指定版本。
     *
     * @param promptCode Prompt 编码
     * @param request    下线请求
     * @return 下线后版本
     */
    public PromptResult offline(String promptCode, PromptOfflineRequest request) {
        String version = Objects.requireNonNull(request.version(), "version 不能为空");
        PromptTemplate target = findVersion(promptCode, version);
        if (target.lifecycleStatus() == PromptLifecycleStatus.OFFLINE) {
            return toResult(target);
        }
        PromptTemplate offline = offlineCopy(target);
        promptTemplateRepository.save(offline);
        releasePointerRepository.findByPromptCode(promptCode).ifPresent(pointer -> {
            if (version.equals(pointer.baselineVersion())) {
                releasePointerRepository.save(new PromptReleasePointer(
                        pointer.tenantCode(),
                        pointer.promptCode(),
                        version,
                        null,
                        null
                ));
            }
        });
        log.info("下线 Prompt 版本, promptCode={}, version={}", promptCode, version);
        return toResult(offline);
    }

    /**
     * 配置灰度规则。
     *
     * @param promptCode Prompt 编码
     * @param request    灰度请求
     * @return 发布指针摘要
     */
    public PromptReleasePointer configureGray(String promptCode, PromptGrayRequest request) {
        PromptGrayRule grayRule = Objects.requireNonNull(request.grayRule(), "grayRule 不能为空");
        findVersion(promptCode, grayRule.baselineVersion());
        findVersion(promptCode, grayRule.candidateVersion());
        PromptReleasePointer pointer = new PromptReleasePointer(
                TenantContext.currentTenantCode(),
                promptCode,
                grayRule.baselineVersion(),
                grayRule.candidateVersion(),
                grayRule
        );
        releasePointerRepository.save(pointer);
        log.info("配置 Prompt 灰度, promptCode={}, baseline={}, candidate={}, percentage={}",
                promptCode, grayRule.baselineVersion(), grayRule.candidateVersion(), grayRule.percentage());
        return pointer;
    }

    private PromptTemplate resolveBaseTemplate(String promptCode, PromptDraftRequest request) {
        List<PromptTemplate> versions = promptTemplateRepository.listVersionsByPromptCode(promptCode);
        if (versions.isEmpty()) {
            if (request == null || !StringUtils.hasText(request.templateContent())) {
                throw Errors.of(PlatformErrorCode.METADATA_BASELINE_NOT_FOUND, "未找到 Prompt 基线版本: " + promptCode);
            }
            return new PromptTemplate(
                    promptCode,
                    StringUtils.hasText(request.promptName()) ? request.promptName() : promptCode,
                    StringUtils.hasText(request.scenarioCode()) ? request.scenarioCode() : promptCode,
                    "1.0.0",
                    request.templateContent(),
                    request.variableSchema(),
                    request.outputSchema(),
                    CommonStatus.ENABLED,
                    null,
                    PromptLifecycleStatus.DRAFT
            );
        }
        return versions.stream()
                .max(Comparator.comparing(PromptTemplate::version))
                .orElseThrow();
    }

    private String resolveDraftVersion(String promptCode, String requestedVersion) {
        if (StringUtils.hasText(requestedVersion)) {
            promptTemplateRepository.findByPromptCodeAndVersion(promptCode, requestedVersion)
                    .ifPresent(existing -> {
                        throw Errors.of(PlatformErrorCode.METADATA_VERSION_EXISTS, "版本已存在: " + requestedVersion);
                    });
            return requestedVersion;
        }
        List<PromptTemplate> versions = promptTemplateRepository.listVersionsByPromptCode(promptCode);
        String latest = versions.stream()
                .map(PromptTemplate::version)
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

    private PromptTemplate findVersion(String promptCode, String version) {
        return promptTemplateRepository.findByPromptCodeAndVersion(promptCode, version)
                .orElseThrow(() -> Errors.of(PlatformErrorCode.PROMPT_NOT_FOUND,
                        "未找到 Prompt 版本: " + promptCode + "@" + version));
    }

    private void ensureAnyVersionExists(String promptCode) {
        if (promptTemplateRepository.listVersionsByPromptCode(promptCode).isEmpty()) {
            throw Errors.of(PlatformErrorCode.PROMPT_NOT_FOUND, "未找到 Prompt: " + promptCode);
        }
    }

    private PromptTemplate offlineCopy(PromptTemplate template) {
        return new PromptTemplate(
                template.promptCode(),
                template.promptName(),
                template.scenarioCode(),
                template.version(),
                template.templateContent(),
                template.variableSchema(),
                template.outputSchema(),
                template.status(),
                template.publishedAt(),
                PromptLifecycleStatus.OFFLINE
        );
    }

    private PromptResult toResult(PromptTemplate definition) {
        return new PromptResult(
                definition.promptCode(),
                definition.promptName(),
                definition.scenarioCode(),
                definition.version(),
                definition.templateContent(),
                definition.variableSchema(),
                definition.outputSchema(),
                definition.status(),
                definition.lifecycleStatus(),
                definition.publishedAt()
        );
    }
}
