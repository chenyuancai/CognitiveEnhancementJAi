package cn.cyc.ai.cog.runtime.release.router;

import cn.cyc.ai.cog.core.metadata.prompt.PromptGrayRule;
import cn.cyc.ai.cog.core.metadata.prompt.PromptReleasePointer;
import cn.cyc.ai.cog.core.metadata.prompt.PromptReleasePointerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Prompt 发布选版路由器，支持简版百分比灰度。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Component
public class PromptReleaseRouter {

    /** 日志记录器 */
    private static final Logger log = LoggerFactory.getLogger(PromptReleaseRouter.class);

    /** releasePointer仓储。 */
    private final PromptReleasePointerRepository releasePointerRepository;

    /**
     * 创建PromptReleaseRouter。
     *
     * @param releasePointerRepository releasePointer仓储
     */
    public PromptReleaseRouter(PromptReleasePointerRepository releasePointerRepository) {
        this.releasePointerRepository = releasePointerRepository;
    }

    /**
     * 解析运行时使用的 Prompt 版本。
     *
     * @param promptCode      Prompt 编码
     * @param traceId         链路 ID
     * @param defaultVersion  默认版本（通常为当前 PUBLISHED 版本）
     * @return 选中的版本号
     */
    public String resolveVersion(String promptCode, String traceId, String defaultVersion) {
        return releasePointerRepository.findByPromptCode(promptCode)
                .map(pointer -> resolveWithPointer(pointer, traceId, defaultVersion))
                .orElse(defaultVersion);
    }

    /**
     * 执行resolveWithPointer。
     *
     * @param pointer pointer
     * @param traceId 链路 Trace ID
     * @param defaultVersion 默认版本号
     * @return 执行结果
     */
    private String resolveWithPointer(PromptReleasePointer pointer, String traceId, String defaultVersion) {
        PromptGrayRule grayRule = pointer.grayRule();
        if (grayRule == null
                || !StringUtils.hasText(grayRule.candidateVersion())
                || grayRule.percentage() <= 0) {
            return StringUtils.hasText(pointer.baselineVersion()) ? pointer.baselineVersion() : defaultVersion;
        }
        String hashSource = StringUtils.hasText(traceId) ? traceId : defaultVersion;
        int bucket = Math.floorMod(hashSource.hashCode(), 100);
        boolean useCandidate = bucket < grayRule.percentage();
        String selected = useCandidate ? grayRule.candidateVersion() : grayRule.baselineVersion();
        log.debug("Prompt 灰度选版, promptCode={}, traceId={}, bucket={}, selectedVersion={}, candidate={}, baseline={}",
                pointer.promptCode(), traceId, bucket, selected, grayRule.candidateVersion(), grayRule.baselineVersion());
        return selected;
    }
}
