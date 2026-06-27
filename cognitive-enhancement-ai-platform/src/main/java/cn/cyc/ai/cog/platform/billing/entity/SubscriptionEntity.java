package cn.cyc.ai.cog.platform.billing.entity;

import cn.cyc.ai.cog.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("qz_bill_subscription")
public class SubscriptionEntity extends BaseEntity {

    private Long accountId;
    private Long orderId;
    private Long packageId;
    private String levelCode;
    private String status;
    private String phase;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private Boolean autoRenew;
    private String packageSnapshotJson;
}
