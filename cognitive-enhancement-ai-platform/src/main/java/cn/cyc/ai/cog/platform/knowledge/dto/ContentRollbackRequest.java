package cn.cyc.ai.cog.platform.knowledge.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 内容版本回滚请求。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class ContentRollbackRequest {

    /** 主键 ID */
    private Long id;

    /** 版本号，每次更新递增 */
    @NotNull(message = "versionNo 不能为空")
    @Min(value = 1, message = "versionNo 必须大于 0")
    private Integer versionNo;
}
