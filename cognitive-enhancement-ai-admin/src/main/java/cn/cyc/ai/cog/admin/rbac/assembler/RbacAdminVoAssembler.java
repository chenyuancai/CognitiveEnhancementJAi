package cn.cyc.ai.cog.admin.rbac.assembler;

import cn.cyc.ai.cog.admin.rbac.dto.PermissionVO;
import cn.cyc.ai.cog.admin.rbac.entity.PermissionEntity;
import org.springframework.stereotype.Component;

/**
 * 管理端 RBAC 域 Entity → VO 转换器。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Component
public class RbacAdminVoAssembler {

    /**
     * 权限点实体转 VO。
     *
     * @param entity 权限点实体
     * @return 权限点 VO
     */
    public PermissionVO toPermissionVo(PermissionEntity entity) {
        PermissionVO vo = new PermissionVO();
        vo.setId(entity.getId());
        vo.setPermissionCode(entity.getPermissionCode());
        vo.setAliasCode(entity.getAliasCode());
        vo.setPermissionName(entity.getPermissionName());
        vo.setParentId(entity.getParentId());
        vo.setPath(entity.getPath());
        vo.setComponent(entity.getComponent());
        vo.setIcon(entity.getIcon());
        vo.setSortNo(entity.getSortNo());
        vo.setStatus(entity.getStatus());
        vo.setScope(entity.getScope());
        vo.setKind(entity.getKind());
        vo.setModuleKey(entity.getModuleKey());
        vo.setGroupKey(entity.getGroupKey());
        vo.setParentMenuKey(entity.getParentMenuKey());
        vo.setDescription(entity.getDescription());
        vo.setBuiltin(entity.getBuiltin());
        vo.setCreateTime(entity.getCreateTime());
        vo.setUpdateTime(entity.getUpdateTime());
        return vo;
    }
}
