package cn.cyc.ai.cog.core.metadata.prompt;

import java.util.Optional;

/**
 * Prompt 发布指针仓储接口。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public interface PromptReleasePointerRepository {

    /**
     * 按 Prompt 编码查询发布指针。
     *
     * @param promptCode Prompt 编码
     * @return 发布指针
     */
    Optional<PromptReleasePointer> findByPromptCode(String promptCode);

    /**
     * 保存发布指针。
     *
     * @param pointer 发布指针
     * @return 保存后的指针
     */
    PromptReleasePointer save(PromptReleasePointer pointer);
}
