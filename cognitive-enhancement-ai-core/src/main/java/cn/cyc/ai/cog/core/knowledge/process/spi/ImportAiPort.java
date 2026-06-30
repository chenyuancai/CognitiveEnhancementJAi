package cn.cyc.ai.cog.core.knowledge.process.spi;

import java.util.List;
import java.util.Map;

/**
 * 导入链路 AI 能力端口（摘要、向量化等）。
 */
public interface ImportAiPort {

    String summarize(String title, String markdown);

    List<Float> embed(String text);

    /**
     * 可选：根据正文生成测验题草稿（autoQuiz 时由上层调用）。
     */
    Map<String, Object> generateQuizDraft(String title, String markdown);
}
