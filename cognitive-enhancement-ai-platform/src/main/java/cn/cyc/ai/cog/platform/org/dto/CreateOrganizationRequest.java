package cn.cyc.ai.cog.platform.org.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateOrganizationRequest {

    @NotBlank
    private String orgName;

    /** 2B / 2G */
    @NotBlank
    private String segment;

    @NotNull
    private Long ownerUserId;

    private String unifiedSocialCode;
    private Integer seatLimit;
    private String contactName;
    private String contactPhone;
}
