package cn.cyc.ai.cog.admin.operation.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Banner视图对象
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class BannerVO {

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
    /** 状态。 */
    private String status;
    /** start时间。 */
    private LocalDateTime startTime;
    /** end时间。 */
    private LocalDateTime endTime;
}
