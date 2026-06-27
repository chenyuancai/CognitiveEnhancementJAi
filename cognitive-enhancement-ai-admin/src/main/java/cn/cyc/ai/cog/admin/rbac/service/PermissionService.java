package cn.cyc.ai.cog.admin.rbac.service;

import cn.cyc.ai.cog.common.exception.Errors;
import cn.cyc.ai.cog.common.exception.PlatformErrorCode;
import cn.cyc.ai.cog.admin.rbac.dto.PermissionSaveRequest;
import cn.cyc.ai.cog.admin.rbac.dto.PermissionTreeGroup;
import cn.cyc.ai.cog.admin.rbac.entity.PermissionEntity;
import cn.cyc.ai.cog.admin.rbac.repository.PermissionRepository;
import cn.cyc.ai.cog.common.constant.CommonConstants;
import cn.cyc.ai.cog.common.exception.ServiceException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 权限点管理服务：权限树、自定义权限 CRUD。
 *
 * @author cyc
 */
@Service
public class PermissionService {

    /** 权限点仓储 */
    private final PermissionRepository permissionRepository;

    /**
     * @param permissionRepository 权限点仓储
     */
    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    /** 查询全部权限点（平铺）。 */
    public List<PermissionEntity> listAll() {
        return permissionRepository.listAll();
    }

    /** 查询自定义权限点（builtin=false）。 */
    public List<PermissionEntity> listCustom() {
        return permissionRepository.listCustom();
    }

    /** 构建权限树（按 scope + moduleKey 分组）。 */
    public List<PermissionTreeGroup> buildTree(String scope) {
        List<PermissionEntity> permissions = permissionRepository.listEnabledByScope(scope);

        Map<String, PermissionTreeGroup> groupMap = new LinkedHashMap<>();
        for (PermissionEntity permission : permissions) {
            String groupKey = StringUtils.hasText(permission.getModuleKey())
                    ? permission.getModuleKey() : "default";
            String mapKey = permission.getScope() + ":" + groupKey;
            PermissionTreeGroup group = groupMap.computeIfAbsent(mapKey, key -> {
                PermissionTreeGroup g = new PermissionTreeGroup();
                g.setScope(permission.getScope());
                g.setKey(groupKey);
                g.setName(groupKey);
                g.setItems(new ArrayList<>());
                return g;
            });
            PermissionTreeGroup.PermissionTreeItem item = new PermissionTreeGroup.PermissionTreeItem();
            item.setCode(StringUtils.hasText(permission.getAliasCode())
                    ? permission.getAliasCode() : permission.getPermissionCode());
            item.setName(permission.getPermissionName());
            item.setKind(permission.getKind());
            item.setBindKey(permission.getParentMenuKey());
            item.setPath(permission.getPath());
            group.getItems().add(item);
        }
        return new ArrayList<>(groupMap.values());
    }

    /** 校验权限码是否可用。 */
    public Map<String, Object> checkCode(String code, String scope, Long excludeId) {
        if (!StringUtils.hasText(code)) {
            throw Errors.of(PlatformErrorCode.PERMISSION_CODE_REQUIRED);
        }
        return Map.of("available", permissionRepository.isCodeAvailable(code, excludeId));
    }

    /** 新增自定义权限点。 */
    public PermissionEntity create(PermissionSaveRequest request) {
        Map<String, Object> check = checkCode(request.getCode(), request.getScope(), null);
        if (!(Boolean) check.get("available")) {
            throw Errors.of(PlatformErrorCode.PERMISSION_CODE_EXISTS, "权限码已存在：" + request.getCode());
        }
        PermissionEntity entity = toEntity(request);
        entity.setBuiltin(false);
        return permissionRepository.insert(entity);
    }

    /** 编辑自定义权限点。 */
    public PermissionEntity update(Long id, PermissionSaveRequest request) {
        PermissionEntity entity = permissionRepository.requireById(id);
        if (Boolean.TRUE.equals(entity.getBuiltin())) {
            throw Errors.of(PlatformErrorCode.BUILTIN_PERMISSION_NOT_EDITABLE);
        }
        entity.setPermissionName(request.getName().trim());
        entity.setDescription(request.getDescription());
        entity.setModuleKey(request.getModuleKey());
        entity.setGroupKey(request.getGroupKey());
        entity.setParentMenuKey(request.getParentMenuKey());
        entity.setPath(request.getPath());
        if (StringUtils.hasText(request.getStatus())) {
            entity.setStatus(request.getStatus());
        }
        return permissionRepository.update(entity);
    }

    /** 删除自定义权限点。 */
    public void delete(Long id) {
        PermissionEntity entity = permissionRepository.requireById(id);
        if (Boolean.TRUE.equals(entity.getBuiltin())) {
            throw Errors.of(PlatformErrorCode.BUILTIN_PERMISSION_NOT_DELETABLE);
        }
        permissionRepository.deleteById(id);
    }

    private PermissionEntity toEntity(PermissionSaveRequest request) {
        PermissionEntity entity = new PermissionEntity();
        entity.setPermissionCode(request.getCode().trim());
        entity.setPermissionName(request.getName().trim());
        entity.setScope(request.getScope());
        entity.setKind(request.getKind());
        entity.setModuleKey(request.getModuleKey());
        entity.setGroupKey(request.getGroupKey());
        entity.setParentMenuKey(request.getParentMenuKey());
        entity.setPath(request.getPath());
        entity.setDescription(request.getDescription());
        entity.setParentId(0L);
        entity.setSortNo(999);
        entity.setStatus(StringUtils.hasText(request.getStatus())
                ? request.getStatus() : CommonConstants.STATUS_ENABLED);
        return entity;
    }
}
