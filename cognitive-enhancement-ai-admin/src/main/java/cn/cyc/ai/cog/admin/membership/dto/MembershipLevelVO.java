package cn.cyc.ai.cog.admin.membership.dto;

import lombok.Data;

@Data
public class MembershipLevelVO {

    private Long id;
    private String levelCode;
    private String levelName;
    private String segment;
    private Boolean isDefault;
    private Integer sortNo;
    private String status;
    private String benefitsJson;
}
