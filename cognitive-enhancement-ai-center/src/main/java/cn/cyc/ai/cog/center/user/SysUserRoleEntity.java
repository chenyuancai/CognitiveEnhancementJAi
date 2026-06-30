package cn.cyc.ai.cog.center.user;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 用户角色关联实体。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
@TableName("qz_iam_user_role")
public class SysUserRoleEntity {

    /** 用户 ID */
    private Long userId;
    /** 角色ID */
    private Long roleId;
}
