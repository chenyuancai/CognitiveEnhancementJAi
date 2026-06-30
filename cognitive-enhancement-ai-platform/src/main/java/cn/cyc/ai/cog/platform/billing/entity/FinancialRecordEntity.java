package cn.cyc.ai.cog.platform.billing.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * FinancialRecord实体
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
@TableName("qz_bill_financial_record")
public class FinancialRecordEntity {

    /** 主键 ID */
    @TableId(type = IdType.AUTO)
    private Long id;
    /** 租户 ID */
    private Long tenantId;
    /** 账户ID */
    private Long accountId;
    /** 订单ID */
    private Long orderId;
    /** record类型。 */
    private String recordType;
    /** 消息。 */
    private String message;
    /** amountFen。 */
    private Long amountFen;
    /** balanceAfterFen。 */
    private Long balanceAfterFen;
    /** remark。 */
    private String remark;
    /** 创建时间 */
    private LocalDateTime createTime;
}
