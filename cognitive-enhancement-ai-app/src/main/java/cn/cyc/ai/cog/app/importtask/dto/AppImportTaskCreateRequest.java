package cn.cyc.ai.cog.app.importtask.dto;

import lombok.Data;

import java.util.List;

/**
 * 创建导入任务请求。
 *
 * @author cyc
 * @date 2026/6/29
 */
@Data
public class AppImportTaskCreateRequest {

    /** 业务类型：KNOWLEDGE_DOCUMENT 等 */
    private String importBizType;

    /** 导入渠道：file / url 等 */
    private String channel;

    /** base 文件 ID（先 POST /api/app/files/upload） */
    private Long fileId;

    /** base:// 文件引用，与 fileId 二选一 */
    private String fileUrl;

    /** 任务标题 */
    private String title;

    /** 文件名 */
    private String fileName;

    /** 目标类型：knowledge 等 */
    private String targetType;

    /** 标签 */
    private List<String> tags;

    /** 是否 AI 增强 */
    private Boolean aiEnhanced;

    /** 是否自动生成测验 */
    private Boolean autoQuiz;
}
