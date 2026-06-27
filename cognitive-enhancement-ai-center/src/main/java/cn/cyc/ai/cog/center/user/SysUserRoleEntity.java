package cn.cyc.ai.cog.center.user;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 用户角色关联实体。
 *
 * @author cyc
 */
@Data
@TableName("qz_iam_user_role")
public class SysUserRoleEntity {

    private Long userId;
    private Long roleId;
}
