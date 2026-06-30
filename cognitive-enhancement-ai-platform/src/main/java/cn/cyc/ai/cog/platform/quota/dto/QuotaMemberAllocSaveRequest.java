package cn.cyc.ai.cog.platform.quota.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 额度MemberAllocSave请求
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class QuotaMemberAllocSaveRequest {

    /** 主键 ID */
    private Long id;

    /** 用户 ID */
    @NotNull
    private Long userId;

    /** allocatedAmount。 */
    @NotNull
    @Min(1)
    private Long allocatedAmount;
}
