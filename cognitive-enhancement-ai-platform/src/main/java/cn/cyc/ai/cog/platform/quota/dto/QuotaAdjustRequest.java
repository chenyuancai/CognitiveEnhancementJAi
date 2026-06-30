package cn.cyc.ai.cog.platform.quota.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 额度Adjust请求
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class QuotaAdjustRequest {

    /** 账户ID */
    private Long accountId;

    /** bucket。 */
    @NotBlank
    private String bucket;

    /** deltaAmount。 */
    @NotNull
    private Long deltaAmount;

    /** remark。 */
    private String remark;
}
