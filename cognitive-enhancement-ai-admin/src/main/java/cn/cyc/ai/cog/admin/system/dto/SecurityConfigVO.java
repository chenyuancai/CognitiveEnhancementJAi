package cn.cyc.ai.cog.admin.system.dto;

import lombok.Data;

@Data
public class SecurityConfigVO {

    private Long id;
    private String configKey;
    private String configValue;
    private String description;
}
