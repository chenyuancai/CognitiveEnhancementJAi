package cn.cyc.ai.cog.admin.membership.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 会员ChangeLog视图对象
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class MembershipChangeLogVO {

    /** 主键 ID */
    private Long id;
    /** 租户 ID */
    private Long tenantId;
    /** 账户ID */
    private Long accountId;
    /** from等级编码。 */
    private String fromLevelCode;
    /** to等级编码。 */
    private String toLevelCode;
    /** change类型。 */
    private String changeType;
    /** operatorID */
    private Long operatorId;
    /** 消息。 */
    private String message;
    /** remark。 */
    private String remark;
    /** 创建时间 */
    private LocalDateTime createTime;
}
