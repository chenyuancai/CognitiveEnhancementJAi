package cn.cyc.ai.cog.app.tutoring.service;

import cn.cyc.ai.cog.app.tutoring.config.AppTutoringProperties;
import cn.cyc.ai.cog.core.harness.RuntimeHarness;
import cn.cyc.ai.cog.core.runtime.CapabilityExecuteRequest;
import cn.cyc.ai.cog.core.runtime.CapabilityExecuteResponse;
import cn.cyc.ai.cog.core.runtime.ExecutionResult;
import cn.cyc.ai.cog.runtime.session.service.ConversationSessionService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * 会话标题 LLM 重写服务。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Service
public class AppTutoringSessionTitleService {

    /**
     * 运行时会话服务。
     */
    private final ConversationSessionService conversationSessionService;

    /**
     * 运行时能力执行器。
     */
    private final RuntimeHarness runtimeHarness;

    /**
     * 学习辅导配置属性。
     */
    private final AppTutoringProperties properties;

    /**
     * 创建会话标题重写服务。
     *
     * @param conversationSessionService 运行时会话服务
     * @param runtimeHarness             运行时能力执行器
     * @param properties                 学习辅导配置属性
     */
    public AppTutoringSessionTitleService(ConversationSessionService conversationSessionService,
                                          RuntimeHarness runtimeHarness,
                                          AppTutoringProperties properties) {
        this.conversationSessionService = conversationSessionService;
        this.runtimeHarness = runtimeHarness;
        this.properties = properties;
    }

    /**
     * 在配置开启时根据首轮对话重写会话标题。
     *
     * @param sessionId        会话 ID
     * @param userMessage      用户消息
     * @param assistantAnswer  助手回复
     */
    public void rewriteIfEnabled(String sessionId, String userMessage, String assistantAnswer) {
        if (!properties.isTitleRewriteEnabled()) {
            return;
        }
        String title = generateTitle(userMessage, assistantAnswer);
        if (StringUtils.hasText(title)) {
            conversationSessionService.updateTitle(sessionId, title);
        }
    }

    /**
     * 调用 LLM 能力生成会话标题，失败时回退为用户消息摘要。
     *
     * @param userMessage     用户消息
     * @param assistantAnswer 助手回复
     * @return 不超过 20 字的标题
     */
    private String generateTitle(String userMessage, String assistantAnswer) {
        try {
            String prompt = """
                    请为以下学习辅导会话生成不超过20字的标题，只输出标题文本。
                    用户: %s
                    助手: %s
                    """.formatted(abbreviate(userMessage), abbreviate(assistantAnswer));
            CapabilityExecuteResponse response = runtimeHarness.execute(new CapabilityExecuteRequest(
                    properties.getAnalyzeCapabilityCode(),
                    Map.of("question", prompt),
                    Map.of("conversationEnabled", false, "titleMode", true)));
            String title = extractText(response).trim().replaceAll("[\\r\\n]+", "");
            return title.length() <= 20 ? title : title.substring(0, 20);
        } catch (Exception ex) {
            return abbreviate(userMessage);
        }
    }

    /**
     * 从能力执行响应中提取文本输出。
     *
     * @param response 能力执行响应
     * @return 文本内容
     */
    private String extractText(CapabilityExecuteResponse response) {
        if (response == null || response.result() == null) {
            return "";
        }
        ExecutionResult result = response.result();
        if (StringUtils.hasText(result.message())) {
            return result.message();
        }
        Object businessOutput = result.output().get("businessOutput");
        return businessOutput == null ? "" : String.valueOf(businessOutput);
    }

    /**
     * 执行abbreviate。
     *
     * @param text text
     * @return 执行结果
     */
    private String abbreviate(String text) {
        if (!StringUtils.hasText(text)) {
            return "";
        }
        String compact = text.trim();
        return compact.length() <= 40 ? compact : compact.substring(0, 40);
    }
}
