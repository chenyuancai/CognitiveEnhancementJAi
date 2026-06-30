package cn.cyc.ai.cog.app.tutoring.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * C 端学习辅导引用上下文。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class AppTutoringReferences {

    /** 引用的历史消息 ID。 */
    private List<String> messageIds = new ArrayList<>();

    /** 引用的文件 ID。 */
    private List<String> fileIds = new ArrayList<>();

    /** 引用的知识 ID。 */
    private List<String> knowledgeIds = new ArrayList<>();

    /** 前端选中的原文片段。 */
    private String selectedText;
}
