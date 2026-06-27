package cn.cyc.ai.cog.auth.user;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 认证用户 Mapper：加载用户与其角色编码。
 *
 * @author cyc
 */
@Mapper
public interface AuthUserMapper extends BaseMapper<AuthUserEntity> {

    /** 查询用户拥有的角色编码列表。 */
    @Select("SELECT r.role_code FROM qz_iam_role r "
            + "JOIN qz_iam_user_role ur ON ur.role_id = r.id "
            + "WHERE ur.user_id = #{userId}")
    List<String> selectRoleCodes(Long userId);

    /** 查询用户拥有的权限点编码列表（优先 alias）。 */
    @Select("SELECT DISTINCT COALESCE(NULLIF(p.alias_code, ''), p.permission_code) FROM qz_iam_permission p "
            + "JOIN qz_iam_role_permission rp ON rp.permission_id = p.id "
            + "JOIN qz_iam_user_role ur ON ur.role_id = rp.role_id "
            + "WHERE ur.user_id = #{userId}")
    List<String> selectPermissionCodes(Long userId);

    /** 按租户 ID 查询租户编码。 */
    @Select("SELECT tenant_code FROM qz_iam_tenant WHERE id = #{tenantId}")
    String selectTenantCodeById(Long tenantId);
}
