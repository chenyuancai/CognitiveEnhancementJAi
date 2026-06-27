package cn.cyc.ai.cog.admin.rbac.mapper;

import cn.cyc.ai.cog.admin.rbac.entity.RoleEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 角色 Mapper。
 *
 * @author cyc
 */
@Mapper
public interface RoleMapper extends BaseMapper<RoleEntity> {

    /** 统计角色下的成员数。 */
    @Select("SELECT COUNT(*) FROM qz_iam_user_role WHERE role_id = #{roleId}")
    long countMembers(Long roleId);
}
