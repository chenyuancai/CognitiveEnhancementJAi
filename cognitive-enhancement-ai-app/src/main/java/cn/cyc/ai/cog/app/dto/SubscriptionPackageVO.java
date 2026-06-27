package cn.cyc.ai.cog.app.dto;

import lombok.Data;

/**
 * C 端订阅套餐展示 VO。
 */
@Data
public class SubscriptionPackageVO {

    /** 套餐 ID */
    private Long id;

    /** 套餐编码 */
    private String packageCode;

    /** 套餐名称 */
    private String packageName;

    /** 客群分段（2C/2B） */
    private String segment;

    /** 关联会员等级 ID */
    private Long levelId;

    /** 计费周期（MONTH/YEAR 等） */
    private String billingPeriod;

    /** 周期数量 */
    private Integer periodCount;

    /** 试用期天数（0=无） */
    private Integer trialDays;

    /** 售价（分） */
    private Long priceFen;

    /** 原价（分） */
    private Long originalPriceFen;

    /** 每周期 Token 额度 */
    private Long cycleTokenQuota;

    /** 席位数（B 端） */
    private Integer seatCount;

    /** 销售模式 */
    private String saleMode;

    /** 是否需合同 */
    private Boolean requireContract;

    /** 上架状态 */
    private String status;
}
