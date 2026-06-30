package cn.cyc.ai.cog.platform.practice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 练习作答实体。
 */
@Data
@TableName("qz_app_practice_answer")
public class PracticeAnswerEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("tenant_id")
    private Long tenantId;

    @TableField("session_id")
    private Long sessionId;

    @TableField("question_id")
    private String questionId;

    @TableField("question_type")
    private String questionType;

    @TableField("answer_payload_json")
    private String answerPayloadJson;

    private Integer score;

    @TableField("ai_feedback_json")
    private String aiFeedbackJson;

    private String status;

    @TableField("create_time")
    private LocalDateTime createTime;
}
