package cn.cyc.ai.cog.app.tutoring.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 本轮实际使用的上下文摘要。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class AppTutoringContextUsed {

    /** 是否使用了最近会话消息。 */
    private boolean recentMessages;

    /** 是否使用了历史摘要。 */
    private boolean summary;

    /** 引用的知识 ID 列表。 */
    private List<String> knowledgeRefs = new ArrayList<>();

    /** 引用的文件 ID 列表。 */
    private List<String> fileRefs = new ArrayList<>();

    /** 引用的历史消息 ID 列表。 */
    private List<String> messageRefs = new ArrayList<>();

    /** 前端选中的原文。 */
    private String selectedText;
}
