package cn.cyc.ai.cog.app.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * C 端 Banner 展示 VO。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class AppBannerVO {

    /** 主键 ID */
    private Long id;
    /** 标题。 */
    private String title;
    /** image地址。 */
    private String imageUrl;
    /** link地址。 */
    private String linkUrl;
    /** position。 */
    private String position;
    /** sortNo。 */
    private Integer sortNo;
    /** start时间。 */
    private LocalDateTime startTime;
    /** end时间。 */
    private LocalDateTime endTime;

    /** 点击行为类型 */
    private String actionType;

    /** 点击跳转地址 */
    private String actionUrl;
}
