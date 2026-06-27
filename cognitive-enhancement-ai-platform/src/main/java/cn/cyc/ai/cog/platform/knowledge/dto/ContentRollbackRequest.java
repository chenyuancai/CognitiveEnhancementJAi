package cn.cyc.ai.cog.platform.knowledge.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 内容版本回滚请求。
 */
@Data
public class ContentRollbackRequest {

    private Long id;

    @NotNull(message = "versionNo 不能为空")
    @Min(value = 1, message = "versionNo 必须大于 0")
    private Integer versionNo;
}
