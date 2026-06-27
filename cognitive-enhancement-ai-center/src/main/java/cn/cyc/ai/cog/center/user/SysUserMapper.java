package cn.cyc.ai.cog.center.user;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 系统用户 Mapper。
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUserEntity> {

    /**
     * 根据用户名查询用户。
     */
    @Select("SELECT * FROM qz_iam_user WHERE username = #{username} LIMIT 1")
    SysUserEntity selectByUsername(@Param("username") String username);

    /**
     * 查询用户的角色编码列表。
     */
    @Select("SELECT r.role_code FROM qz_iam_role r JOIN qz_iam_user_role ur ON r.id = ur.role_id WHERE ur.user_id = #{userId}")
    List<String> selectRoleCodesByUserId(@Param("userId") Long userId);
}
