package cn.cyc.ai.cog.platform.operations.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Banner 新增/编辑请求。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class BannerSaveRequest {

    /** 主键 ID */
    private Long id;

    /** 标题。 */
    @NotBlank(message = "标题不能为空")
    private String title;

    /** image地址。 */
    @NotBlank(message = "图片URL不能为空")
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
