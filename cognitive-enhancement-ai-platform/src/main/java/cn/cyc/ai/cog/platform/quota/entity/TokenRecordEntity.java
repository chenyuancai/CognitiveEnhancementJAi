package cn.cyc.ai.cog.platform.quota.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 令牌Record实体
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
@TableName("qz_mbr_token_record")
public class TokenRecordEntity {

    /** 主键 ID */
    @TableId(type = IdType.AUTO)
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
    /** remark。 */
    private String remark;
    /** 创建时间 */
    private LocalDateTime createTime;
}
