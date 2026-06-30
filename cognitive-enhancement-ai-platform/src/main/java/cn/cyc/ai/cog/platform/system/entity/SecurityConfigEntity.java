package cn.cyc.ai.cog.platform.system.entity;

import cn.cyc.ai.cog.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Security配置实体
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("qz_sys_security_config")
public class SecurityConfigEntity extends BaseEntity {

    /** 配置键。 */
    private String configKey;
    /** 配置值。 */
    private String configValue;
    /** 描述。 */
    private String description;
}
