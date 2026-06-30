package cn.cyc.ai.cog.platform.org.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 创建Organization请求
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class CreateOrganizationRequest {

    /** org名称。 */
    @NotBlank
    private String orgName;

    /** 2B / 2G */
    @NotBlank
    private String segment;

    /** owner用户ID */
    @NotNull
    private Long ownerUserId;

    /** unifiedSocial编码。 */
    private String unifiedSocialCode;
    /** seat限制。 */
    private Integer seatLimit;
    /** contact名称。 */
    private String contactName;
    /** contact手机号。 */
    private String contactPhone;
}
