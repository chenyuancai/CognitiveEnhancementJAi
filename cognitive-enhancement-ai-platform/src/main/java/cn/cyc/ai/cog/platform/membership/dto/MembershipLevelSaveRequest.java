package cn.cyc.ai.cog.platform.membership.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MembershipLevelSaveRequest {

    private Long id;

    @NotBlank
    private String levelCode;

    @NotBlank
    private String levelName;

    @NotBlank
    private String segment;

    private Boolean isDefault;
    private Integer sortNo;
    private String status;
    private String benefitsJson;
}
