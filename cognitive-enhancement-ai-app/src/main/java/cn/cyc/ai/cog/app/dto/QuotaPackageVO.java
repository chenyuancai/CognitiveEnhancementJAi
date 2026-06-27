package cn.cyc.ai.cog.app.dto;

import lombok.Data;

/**
 * C 端额度包展示 VO。
 */
@Data
public class QuotaPackageVO {

    /** 额度包 ID */
    private Long id;

    /** 套餐编码 */
    private String packageCode;

    /** 套餐名称 */
    private String packageName;

    /** 客群分段 */
    private String segment;

    /** Token 数量 */
    private Long tokenAmount;

    /** 售价（分） */
    private Long priceFen;

    /** 上架状态 */
    private String status;
}
