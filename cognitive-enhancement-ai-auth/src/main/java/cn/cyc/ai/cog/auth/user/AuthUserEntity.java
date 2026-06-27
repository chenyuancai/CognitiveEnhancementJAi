package cn.cyc.ai.cog.auth.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 认证用户实体（映射 qz_iam_user，仅取认证所需字段）。
 *
 * @author cyc
 */
@Data
@TableName("qz_iam_user")
public class AuthUserEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String username;

    private String passwordHash;

    private Long tenantId;

    private String status;

    private java.time.LocalDateTime banUntil;
}
