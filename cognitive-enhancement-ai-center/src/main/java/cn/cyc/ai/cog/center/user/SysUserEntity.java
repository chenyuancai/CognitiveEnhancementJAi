package cn.cyc.ai.cog.center.user;

import cn.cyc.ai.cog.runtime.base.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 系统用户数据库实体。
 *
 * @author cyc
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("qz_iam_user")
public class SysUserEntity extends BaseEntity {

    private String username;
    private Long tenantId;
    private String passwordHash;
    private String nickname;
    private String email;
    private String phone;
    private String avatarUrl;
    private String status;
    private LocalDateTime lastLoginTime;
}
