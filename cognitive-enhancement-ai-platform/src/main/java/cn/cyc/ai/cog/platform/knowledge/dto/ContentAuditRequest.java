package cn.cyc.ai.cog.platform.knowledge.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 内容审核请求。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class ContentAuditRequest {

    /** 主键 ID */
    private Long id;

    /** 是否通过：true 发布，false 驳回。 */
    @NotNull(message = "审核结果不能为空")
    private Boolean pass;

    /** 审核备注。 */
    private String remark;
}
