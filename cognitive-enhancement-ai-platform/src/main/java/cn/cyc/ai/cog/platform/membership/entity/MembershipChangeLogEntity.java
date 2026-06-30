package cn.cyc.ai.cog.platform.membership.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 会员ChangeLog实体
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
@TableName("qz_mbr_membership_change_log")
public class MembershipChangeLogEntity {

    /** 主键 ID */
    @TableId(type = IdType.AUTO)
    private Long id;
    /** 租户 ID */
    private Long tenantId;
    /** 账户ID */
    private Long accountId;
    /** 用户 ID */
    private Long userId;
    /** from等级编码。 */
    private String fromLevelCode;
    /** to等级编码。 */
    private String toLevelCode;
    /** change类型。 */
    private String changeType;
    /** 消息。 */
    private String message;
    /** 订单ID */
    private Long orderId;
    /** operatorID */
    private Long operatorId;
    /** remark。 */
    private String remark;
    /** 创建时间 */
    private LocalDateTime createTime;
}
