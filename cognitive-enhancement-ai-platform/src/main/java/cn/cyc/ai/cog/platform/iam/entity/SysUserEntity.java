package cn.cyc.ai.cog.platform.iam.entity;

import cn.cyc.ai.cog.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 系统用户（映射 qz_iam_user）。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("qz_iam_user")
public class SysUserEntity extends BaseEntity {

    private String username;
    private String passwordHash;
    private String nickname;
    private String email;
    private String phone;
    private String avatarUrl;
    private String status;
    private String userType;
    private String banReason;
    private LocalDateTime banUntil;
    private LocalDateTime lastLoginTime;
    private Long primaryAccountId;
}
