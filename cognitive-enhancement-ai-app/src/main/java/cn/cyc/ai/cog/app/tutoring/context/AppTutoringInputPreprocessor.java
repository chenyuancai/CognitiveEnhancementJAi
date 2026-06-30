package cn.cyc.ai.cog.app.tutoring.context;

import cn.cyc.ai.cog.app.tutoring.config.AppTutoringProperties;
import cn.cyc.ai.cog.app.tutoring.dto.AppTutoringReferences;
import cn.cyc.ai.cog.common.exception.Errors;
import cn.cyc.ai.cog.common.exception.PlatformErrorCode;
import cn.cyc.ai.cog.platform.file.spi.PlatformFileClient;
import cn.cyc.ai.cog.platform.knowledge.domain.Content;
import cn.cyc.ai.cog.platform.knowledge.service.ContentService;
import cn.cyc.ai.cog.runtime.session.domain.ConversationMessage;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * C 端 AI 助手引用上下文预处理器，将用户引用解析为可注入 Prompt 的片段。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Component
public class AppTutoringInputPreprocessor {

    /** 知识内容服务。 */
    private final ContentService contentService;

    /** 平台文件客户端提供者。 */
    private final ObjectProvider<PlatformFileClient> platformFileClientProvider;

    /** 学习辅导配置属性。 */
    private final AppTutoringProperties properties;

    /**
     * 构造引用上下文预处理器。
     *
     * @param contentService              知识内容服务
     * @param platformFileClientProvider  平台文件客户端提供者
     * @param properties                  学习辅导配置属性
     */
    public AppTutoringInputPreprocessor(ContentService contentService,
                                        ObjectProvider<PlatformFileClient> platformFileClientProvider,
                                        AppTutoringProperties properties) {
        this.contentService = contentService;
        this.platformFileClientProvider = platformFileClientProvider;
        this.properties = properties;
    }

    /**
     * 解析用户引用并组装结构化上下文片段。
     *
     * @param references      用户引用
     * @param sessionMessages 当前会话消息列表
     * @return 解析后的引用上下文
     */
    public AppTutoringResolvedContext resolve(AppTutoringReferences references,
                                              List<ConversationMessage> sessionMessages) {
        AppTutoringResolvedContext resolved = new AppTutoringResolvedContext();
        if (references == null) {
            return resolved;
        }
        if (StringUtils.hasText(references.getSelectedText())) {
            resolved.setSelectedText(truncate(references.getSelectedText()));
        }
        resolveMessages(references, sessionMessages, resolved);
        resolveKnowledge(references, resolved);
        resolveFiles(references, resolved);
        return resolved;
    }

    /**
     * 解析引用的历史消息片段。
     *
     * @param references      用户引用
     * @param sessionMessages 当前会话消息列表
     * @param resolved        解析结果容器
     */
    private void resolveMessages(AppTutoringReferences references,
                                 List<ConversationMessage> sessionMessages,
                                 AppTutoringResolvedContext resolved) {
        if (CollectionUtils.isEmpty(references.getMessageIds())) {
            return;
        }
        Map<String, ConversationMessage> indexed = sessionMessages.stream()
                .collect(Collectors.toMap(ConversationMessage::messageId, Function.identity(), (a, b) -> a));
        for (String messageId : references.getMessageIds()) {
            ConversationMessage message = indexed.get(messageId);
            if (message == null) {
                throw Errors.of(PlatformErrorCode.BAD_REQUEST, "引用消息不存在: " + messageId);
            }
            AppTutoringResolvedContext.ResolvedMessageSnippet snippet =
                    new AppTutoringResolvedContext.ResolvedMessageSnippet();
            snippet.setMessageId(message.messageId());
            snippet.setRole(message.role().name());
            snippet.setContent(truncate(message.content()));
            resolved.getMessageSnippets().add(snippet);
        }
    }

    /**
     * 解析引用的知识内容片段。
     *
     * @param references 用户引用
     * @param resolved   解析结果容器
     */
    private void resolveKnowledge(AppTutoringReferences references, AppTutoringResolvedContext resolved) {
        if (CollectionUtils.isEmpty(references.getKnowledgeIds())) {
            return;
        }
        for (String knowledgeId : references.getKnowledgeIds()) {
            Long contentId = parseLongId(knowledgeId, "知识");
            Content content = contentService.detail(contentId);
            AppTutoringResolvedContext.ResolvedKnowledgeSnippet snippet =
                    new AppTutoringResolvedContext.ResolvedKnowledgeSnippet();
            snippet.setKnowledgeId(knowledgeId);
            snippet.setTitle(content.title());
            String body = StringUtils.hasText(content.summary()) ? content.summary() : content.body();
            snippet.setExcerpt(truncate(body));
            resolved.getKnowledgeSnippets().add(snippet);
        }
    }

    /**
     * 解析引用的文件内容片段。
     *
     * @param references 用户引用
     * @param resolved   解析结果容器
     */
    private void resolveFiles(AppTutoringReferences references, AppTutoringResolvedContext resolved) {
        if (CollectionUtils.isEmpty(references.getFileIds())) {
            return;
        }
        PlatformFileClient fileClient = platformFileClientProvider.getIfAvailable();
        if (fileClient == null) {
            return;
        }
        for (String fileIdText : references.getFileIds()) {
            Long fileId = parseLongId(fileIdText, "文件");
            var fileInfo = fileClient.getById(fileId);
            if (fileInfo == null) {
                throw Errors.of(PlatformErrorCode.BAD_REQUEST, "引用文件不存在: " + fileIdText);
            }
            AppTutoringResolvedContext.ResolvedFileSnippet snippet =
                    new AppTutoringResolvedContext.ResolvedFileSnippet();
            snippet.setFileId(fileIdText);
            snippet.setFileName(fileInfo.getOriginalName());
            snippet.setExcerpt(truncate(fileClient.readText(fileId)));
            resolved.getFileSnippets().add(snippet);
        }
    }

    /**
     * 将引用 ID 文本解析为 Long 类型。
     *
     * @param raw   原始 ID 文本
     * @param label 资源类型标签（用于错误提示）
     * @return 解析后的 Long ID
     */
    private Long parseLongId(String raw, String label) {
        try {
            return Long.parseLong(raw.trim());
        } catch (NumberFormatException ex) {
            throw Errors.of(PlatformErrorCode.BAD_REQUEST, label + "引用 ID 无效: " + raw);
        }
    }

    /**
     * 按配置截断引用文本内容。
     *
     * @param text 原始文本
     * @return 截断后的文本
     */
    private String truncate(String text) {
        if (!StringUtils.hasText(text)) {
            return "";
        }
        int max = properties.getReferenceMaxChars();
        String compact = text.trim();
        return compact.length() <= max ? compact : compact.substring(0, max) + "…";
    }
}
