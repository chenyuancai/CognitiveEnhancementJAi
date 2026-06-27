package cn.cyc.ai.cog.platform.system.entity;

import cn.cyc.ai.cog.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("qz_sys_security_config")
public class SecurityConfigEntity extends BaseEntity {

    private String configKey;
    private String configValue;
    private String description;
}
