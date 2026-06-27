package cn.cyc.ai.cog.admin.rbac.mapper;

import cn.cyc.ai.cog.admin.rbac.entity.PermissionEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 权限点 Mapper。
 *
 * @author cyc
 */
@Mapper
public interface PermissionMapper extends BaseMapper<PermissionEntity> {

    /** 查询角色已绑定的权限点编码（含 alias）。 */
    @Select("SELECT COALESCE(NULLIF(p.alias_code, ''), p.permission_code) FROM qz_iam_permission p "
            + "JOIN qz_iam_role_permission rp ON rp.permission_id = p.id "
            + "WHERE rp.role_id = #{roleId}")
    List<String> selectCodesByRoleId(Long roleId);

    /** 查询用户拥有的全部权限点实体。 */
    @Select("SELECT DISTINCT p.* FROM qz_iam_permission p "
            + "JOIN qz_iam_role_permission rp ON rp.permission_id = p.id "
            + "JOIN qz_iam_user_role ur ON ur.role_id = rp.role_id "
            + "WHERE ur.user_id = #{userId} AND p.status = 'ENABLED' "
            + "ORDER BY p.sort_no, p.id")
    List<PermissionEntity> selectByUserId(Long userId);
}
