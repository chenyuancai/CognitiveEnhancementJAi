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
 * @date 2026/6/15 14:18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("qz_iam_user")
public class SysUserEntity extends BaseEntity {

    /** username。 */
    private String username;
    /** 租户 ID */
    private Long tenantId;
    /** 密码Hash。 */
    private String passwordHash;
    /** nickname。 */
    private String nickname;
    /** 邮箱。 */
    private String email;
    /** 手机号。 */
    private String phone;
    /** avatar地址。 */
    private String avatarUrl;
    /** 状态。 */
    private String status;
    /** lastLogin时间。 */
    private LocalDateTime lastLoginTime;
}
