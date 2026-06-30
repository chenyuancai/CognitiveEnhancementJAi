package cn.cyc.ai.cog.platform.iam.entity;

import cn.cyc.ai.cog.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 系统用户（映射 qz_iam_user）。
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
    /** 用户类型。 */
    private String userType;
    /** ban原因。 */
    private String banReason;
    /** banUntil。 */
    private LocalDateTime banUntil;
    /** lastLogin时间。 */
    private LocalDateTime lastLoginTime;
    /** primary账户ID */
    private Long primaryAccountId;
}
