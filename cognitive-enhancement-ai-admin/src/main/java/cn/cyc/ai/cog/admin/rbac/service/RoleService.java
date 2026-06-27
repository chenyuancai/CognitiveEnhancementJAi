package cn.cyc.ai.cog.admin.rbac.service;

import cn.cyc.ai.cog.common.exception.Errors;
import cn.cyc.ai.cog.common.exception.PlatformErrorCode;
import cn.cyc.ai.cog.admin.rbac.dto.AssignPermissionRequest;
import cn.cyc.ai.cog.admin.rbac.dto.RolePageQuery;
import cn.cyc.ai.cog.admin.rbac.dto.RoleResult;
import cn.cyc.ai.cog.admin.rbac.dto.RoleSaveRequest;
import cn.cyc.ai.cog.admin.rbac.entity.PermissionEntity;
import cn.cyc.ai.cog.admin.rbac.entity.RoleEntity;
import cn.cyc.ai.cog.admin.rbac.repository.PermissionRepository;
import cn.cyc.ai.cog.admin.rbac.repository.RoleRepository;
import cn.cyc.ai.cog.common.constant.CommonConstants;
import cn.cyc.ai.cog.common.exception.ServiceException;
import cn.cyc.ai.cog.common.page.PageResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 角色管理服务：角色 CRUD、编码校验与权限点授权（按编码）。
 *
 * @author cyc
 */
@Service
public class RoleService {

    private static final Set<String> BUILTIN_ROLE_CODES = Set.of("ADMIN", "USER");

    /** 角色仓储 */
    private final RoleRepository roleRepository;

    /** 权限点仓储 */
    private final PermissionRepository permissionRepository;

    /**
     * @param roleRepository       角色仓储
     * @param permissionRepository 权限点仓储
     */
    public RoleService(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    /** 分页查询角色。 */
    public PageResult<RoleResult> page(RolePageQuery query) {
        return roleRepository.page(query).map(this::toResult);
    }

    /** 查询全部角色（前端列表契约）。 */
    public List<RoleResult> listAll() {
        return roleRepository.listAll().stream().map(this::toResult).toList();
    }

    /** 角色详情。 */
    public RoleResult getById(Long id) {
        return toResult(roleRepository.requireById(id));
    }

    /** 校验角色编码是否可用。 */
    public Map<String, Object> checkCode(String roleCode, Long excludeId) {
        if (!StringUtils.hasText(roleCode)) {
            throw Errors.of(PlatformErrorCode.ROLE_CODE_REQUIRED);
        }
        return Map.of("available", roleRepository.isCodeAvailable(roleCode, excludeId));
    }

    /** 新增角色。 */
    public RoleResult create(RoleSaveRequest request) {
        if (!StringUtils.hasText(request.getRoleCode())) {
            throw Errors.of(PlatformErrorCode.ROLE_CODE_REQUIRED);
        }
        Map<String, Object> check = checkCode(request.getRoleCode(), null);
        if (!(Boolean) check.get("available")) {
            throw Errors.of(PlatformErrorCode.ROLE_CODE_EXISTS, "角色编码已存在：" + request.getRoleCode());
        }
        RoleEntity entity = new RoleEntity();
        entity.setRoleCode(request.getRoleCode().trim());
        entity.setRoleName(request.getRoleName().trim());
        entity.setDescription(request.getDescription());
        entity.setStatus(StringUtils.hasText(request.getStatus())
                ? request.getStatus() : CommonConstants.STATUS_ENABLED);
        return toResult(roleRepository.insert(entity));
    }

    /** 编辑角色（编码不可改）。 */
    public RoleResult update(Long id, RoleSaveRequest request) {
        RoleEntity entity = roleRepository.requireById(id);
        entity.setRoleName(request.getRoleName().trim());
        entity.setDescription(request.getDescription());
        if (StringUtils.hasText(request.getStatus())) {
            entity.setStatus(request.getStatus());
        }
        return toResult(roleRepository.update(entity));
    }

    /** 删除角色及其权限绑定。 */
    @Transactional
    public void delete(Long id) {
        RoleEntity entity = roleRepository.requireById(id);
        if (CommonConstants.ROLE_ADMIN.equals(entity.getRoleCode()) || BUILTIN_ROLE_CODES.contains(entity.getRoleCode())) {
            throw Errors.of(PlatformErrorCode.BUILTIN_ROLE_NOT_DELETABLE);
        }
        roleRepository.deleteById(id);
    }

    /** 重新绑定角色权限（优先 permissionCodes，兼容 permissionIds）。 */
    @Transactional
    public RoleResult assignPermissions(Long roleId, AssignPermissionRequest request) {
        roleRepository.requireById(roleId);
        roleRepository.replacePermissions(roleId, resolvePermissionIds(request));
        return toResult(roleRepository.requireById(roleId));
    }

    private List<Long> resolvePermissionIds(AssignPermissionRequest request) {
        if (request.getPermissionCodes() != null && !request.getPermissionCodes().isEmpty()) {
            List<PermissionEntity> permissions = permissionRepository.listByCodes(request.getPermissionCodes());
            Map<String, Long> codeToId = permissions.stream()
                    .collect(Collectors.toMap(PermissionEntity::getPermissionCode, PermissionEntity::getId));
            for (String code : request.getPermissionCodes()) {
                if (!codeToId.containsKey(code)) {
                    throw Errors.of(PlatformErrorCode.PERMISSION_NOT_FOUND, "权限点不存在：" + code);
                }
            }
            return request.getPermissionCodes().stream().map(codeToId::get).toList();
        }
        if (request.getPermissionIds() != null) {
            return request.getPermissionIds();
        }
        return List.of();
    }

    private RoleResult toResult(RoleEntity entity) {
        RoleResult result = new RoleResult();
        result.setId(entity.getId());
        result.setRoleCode(entity.getRoleCode());
        result.setRoleName(entity.getRoleName());
        result.setDescription(entity.getDescription());
        result.setBuiltin(BUILTIN_ROLE_CODES.contains(entity.getRoleCode()));
        result.setStatus(entity.getStatus());
        result.setAvatarColor(resolveAvatarColor(entity.getRoleCode()));
        result.setMemberCount(roleRepository.countMembers(entity.getId()));
        result.setPermissionCodes(permissionRepository.listCodesByRoleId(entity.getId()));
        result.setCreateTime(entity.getCreateTime());
        result.setUpdateTime(entity.getUpdateTime());
        return result;
    }

    private String resolveAvatarColor(String roleCode) {
        Map<String, String> colors = new HashMap<>();
        colors.put("ADMIN", "purple");
        colors.put("USER", "blue");
        return colors.getOrDefault(roleCode, "gray");
    }
}
