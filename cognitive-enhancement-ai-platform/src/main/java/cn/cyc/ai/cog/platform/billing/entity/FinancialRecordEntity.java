package cn.cyc.ai.cog.platform.billing.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("qz_bill_financial_record")
public class FinancialRecordEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long accountId;
    private Long orderId;
    private String recordType;
    private String message;
    private Long amountFen;
    private Long balanceAfterFen;
    private String remark;
    private LocalDateTime createTime;
}
