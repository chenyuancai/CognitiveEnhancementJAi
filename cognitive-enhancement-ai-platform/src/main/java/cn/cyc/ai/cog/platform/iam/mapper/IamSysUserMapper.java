package cn.cyc.ai.cog.platform.iam.mapper;

import cn.cyc.ai.cog.platform.iam.entity.SysUserEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cn.cyc.ai.cog.platform.iam.entity.UserType;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * IamSys用户数据访问 Mapper
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Mapper
public interface IamSysUserMapper extends BaseMapper<SysUserEntity> {

    @Select("SELECT r.role_code FROM qz_iam_role r "
            + "JOIN qz_iam_user_role ur ON ur.role_id = r.id WHERE ur.user_id = #{userId}")
    List<String> selectRoleCodes(Long userId);

    @Select("SELECT id FROM qz_iam_role WHERE role_code = #{roleCode} AND deleted = 0 LIMIT 1")
    Long selectRoleIdByCode(String roleCode);

    @Insert("INSERT INTO qz_iam_user_role (user_id, role_id) VALUES (#{userId}, #{roleId})")
    void insertUserRole(Long userId, Long roleId);
}
