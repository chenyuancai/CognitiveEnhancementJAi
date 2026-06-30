package cn.cyc.ai.cog.auth.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 认证用户实体（映射 qz_iam_user，仅取认证所需字段）。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
@TableName("qz_iam_user")
public class AuthUserEntity {

    /** 主键 ID */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** username。 */
    private String username;

    /** 密码Hash。 */
    private String passwordHash;

    /** 租户 ID */
    private Long tenantId;

    /** 状态。 */
    private String status;

    /** banUntil。 */
    private java.time.LocalDateTime banUntil;
}
