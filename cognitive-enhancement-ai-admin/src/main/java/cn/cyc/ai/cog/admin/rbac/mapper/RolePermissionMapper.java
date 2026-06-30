package cn.cyc.ai.cog.admin.rbac.mapper;

import cn.cyc.ai.cog.admin.rbac.entity.RolePermissionEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色-权限关联 Mapper。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Mapper
public interface RolePermissionMapper extends BaseMapper<RolePermissionEntity> {
}
