package cn.cyc.ai.cog.platform.operations.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Banner 新增/编辑请求。
 *
 * @author cyc
 */
@Data
public class BannerSaveRequest {

    private Long id;

    @NotBlank(message = "标题不能为空")
    private String title;

    @NotBlank(message = "图片URL不能为空")
    private String imageUrl;

    private String linkUrl;
    private String position;
    private Integer sortNo;
    private String status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
