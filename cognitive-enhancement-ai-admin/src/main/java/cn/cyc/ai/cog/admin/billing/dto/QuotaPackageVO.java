package cn.cyc.ai.cog.admin.billing.dto;

import lombok.Data;

@Data
public class QuotaPackageVO {

    private Long id;
    private Long tenantId;
    private String packageCode;
    private String packageName;
    private String segment;
    private Long tokenAmount;
    private Long priceFen;
    private Integer validDays;
    private String status;
}
