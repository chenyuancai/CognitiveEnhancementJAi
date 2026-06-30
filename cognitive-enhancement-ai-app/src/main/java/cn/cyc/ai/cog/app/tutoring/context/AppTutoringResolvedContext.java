package cn.cyc.ai.cog.app.tutoring.context;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 解析后的引用上下文，用于注入 Prompt。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class AppTutoringResolvedContext {

    /** 用户选中的文本片段。 */
    private String selectedText;

    /** 引用的历史消息片段列表。 */
    private List<ResolvedMessageSnippet> messageSnippets = new ArrayList<>();

    /** 引用的知识内容片段列表。 */
    private List<ResolvedKnowledgeSnippet> knowledgeSnippets = new ArrayList<>();

    /** 引用的文件内容片段列表。 */
    private List<ResolvedFileSnippet> fileSnippets = new ArrayList<>();

    /**
     * 解析后的历史消息片段。
     *
     * @author cyc
     * @date 2026/6/15 14:18
     */
    @Data
    public static class ResolvedMessageSnippet {

        /** 消息 ID。 */
        private String messageId;

        /** 消息角色。 */
        private String role;

        /** 消息内容摘要。 */
        private String content;
    }

    /**
     * 解析后的知识内容片段。
     *
     * @author cyc
     * @date 2026/6/15 14:18
     */
    @Data
    public static class ResolvedKnowledgeSnippet {

        /** 知识内容 ID。 */
        private String knowledgeId;

        /** 知识标题。 */
        private String title;

        /** 内容摘要。 */
        private String excerpt;
    }

    /**
     * 解析后的文件内容片段。
     *
     * @author cyc
     * @date 2026/6/15 14:18
     */
    @Data
    public static class ResolvedFileSnippet {

        /** 文件 ID。 */
        private String fileId;

        /** 文件名称。 */
        private String fileName;

        /** 文件内容摘要。 */
        private String excerpt;
    }
}
