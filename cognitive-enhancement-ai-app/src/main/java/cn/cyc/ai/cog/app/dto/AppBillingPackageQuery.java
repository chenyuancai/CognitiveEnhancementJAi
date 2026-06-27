package cn.cyc.ai.cog.app.dto;

import lombok.Data;

/**
 * C 端可购套餐查询参数。
 */
@Data
public class AppBillingPackageQuery {

    /** 客群分段，可选。 */
    private String segment;
}
