package cn.cyc.ai.cog.admin.quota.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 令牌Record视图对象
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class TokenRecordVO {

    /** 主键 ID */
    private Long id;
    /** 租户 ID */
    private Long tenantId;
    /** 账户ID */
    private Long accountId;
    /** member用户ID */
    private Long memberUserId;
    /** record类型。 */
    private String recordType;
    /** bucket。 */
    private String bucket;
    /** deltaAmount。 */
    private Long deltaAmount;
    /** balanceAfter。 */
    private Long balanceAfter;
    /** biz类型。 */
    private String bizType;
    /** bizID */
    private String bizId;
    /** idempotency键。 */
    private String idempotencyKey;
    /** 消息。 */
    private String message;
    /** 创建时间 */
    private LocalDateTime createTime;
}
