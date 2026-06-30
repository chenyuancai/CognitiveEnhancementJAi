package cn.cyc.ai.cog.admin.system.dto;

import lombok.Data;

/**
 * Security配置视图对象
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class SecurityConfigVO {

    /** 主键 ID */
    private Long id;
    /** 配置键。 */
    private String configKey;
    /** 配置值。 */
    private String configValue;
    /** 描述。 */
    private String description;
}
