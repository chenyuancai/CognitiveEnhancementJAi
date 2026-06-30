package cn.cyc.ai.cog.platform.importtask.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户导入任务实体。
 */
@Data
@TableName("qz_app_import_task")
public class ImportTaskEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("tenant_id")
    private Long tenantId;

    @TableField("user_id")
    private Long userId;

    @TableField("task_code")
    private String taskCode;

    private String channel;

    @TableField("import_biz_type")
    private String importBizType;

    private String title;

    @TableField("file_name")
    private String fileName;

    @TableField("file_id")
    private Long fileId;

    @TableField("file_url")
    private String fileUrl;

    @TableField("target_type")
    private String targetType;

    @TableField("tags_json")
    private String tagsJson;

    @TableField("ai_enhanced")
    private Boolean aiEnhanced;

    @TableField("auto_quiz")
    private Boolean autoQuiz;

    private String status;

    private String stage;

    private Integer progress;

    @TableField("error_message")
    private String errorMessage;

    @TableField("library_item_id")
    private Long libraryItemId;

    @TableField("result_json")
    private String resultJson;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;
}
