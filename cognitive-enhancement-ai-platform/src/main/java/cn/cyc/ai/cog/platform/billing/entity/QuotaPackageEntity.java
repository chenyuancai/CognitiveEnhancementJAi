package cn.cyc.ai.cog.platform.billing.entity;

import cn.cyc.ai.cog.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("qz_bill_quota_package")
public class QuotaPackageEntity extends BaseEntity {

    private String packageCode;
    private String packageName;
    private String segment;
    private Long tokenAmount;
    private Long priceFen;
    private Integer validDays;
    private String status;
}
