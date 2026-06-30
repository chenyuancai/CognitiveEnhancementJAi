package cn.cyc.ai.cog.core.metadata.prompt;

import cn.cyc.ai.cog.core.metadata.MetadataRepository;

import java.util.List;
import java.util.Optional;

/**
 * Prompt 模板仓储接口。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public interface PromptTemplateRepository extends MetadataRepository<PromptTemplate> {

    /**
     * 按编码与版本查询 Prompt 模板（灰度路由可加载 OFFLINE 基线）。
     *
     * @param promptCode Prompt 编码
     * @param version    版本号
     * @return Prompt 模板
     */
    default Optional<PromptTemplate> findByPromptCodeAndVersion(String promptCode, String version) {
        return listVersionsByPromptCode(promptCode).stream()
                .filter(item -> item.version().equals(version))
                .findFirst();
    }

    /**
     * 列出指定 Prompt 编码的全部版本。
     *
     * @param promptCode Prompt 编码
     * @return 版本列表
     */
    List<PromptTemplate> listVersionsByPromptCode(String promptCode);

    /**
     * 查询当前已发布版本（同 promptCode 至多一条 PUBLISHED）。
     *
     * @param promptCode Prompt 编码
     * @return 已发布版本
     */
    default Optional<PromptTemplate> findPublishedByPromptCode(String promptCode) {
        return listVersionsByPromptCode(promptCode).stream()
                .filter(item -> item.lifecycleStatus() == PromptLifecycleStatus.PUBLISHED)
                .findFirst();
    }
}
