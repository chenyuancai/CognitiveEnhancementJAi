package cn.cyc.ai.cog.app.importtask.dto;

import lombok.Data;

/**
 * 导入任务 VO。
 *
 * @author cyc
 * @date 2026/6/29
 */
@Data
public class AppImportTaskVO {

    /** 任务 ID（taskCode） */
    private String id;

    /** 标题 */
    private String title;

    /** 状态：pending / processing / done / failed */
    private String status;

    /** 当前阶段 */
    private String stage;

    /** 进度 0-100 */
    private Integer progress;

    /** 文件名 */
    private String fileName;

    /** 错误信息 */
    private String error;

    /** 入库后的知识条目 ID */
    private String libraryItemId;
}
