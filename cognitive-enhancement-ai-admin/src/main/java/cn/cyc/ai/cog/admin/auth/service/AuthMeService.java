package cn.cyc.ai.cog.admin.auth.service;

import cn.cyc.ai.cog.common.exception.Errors;
import cn.cyc.ai.cog.common.exception.PlatformErrorCode;
import cn.cyc.ai.cog.admin.auth.assembler.AuthMeVoAssembler;
import cn.cyc.ai.cog.admin.auth.dto.AuthMeResponse;
import cn.cyc.ai.cog.admin.rbac.entity.PermissionEntity;
import cn.cyc.ai.cog.admin.rbac.repository.PermissionRepository;
import cn.cyc.ai.cog.platform.account.dto.UserMeContext;
import cn.cyc.ai.cog.platform.account.service.UserMeContextService;
import cn.cyc.ai.cog.platform.iam.repository.IamUserRepository;
import cn.cyc.ai.cog.common.context.AuthUser;
import cn.cyc.ai.cog.common.context.UserContext;
import cn.cyc.ai.cog.common.exception.ServiceException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 当前登录用户上下文聚合：用户、账户、权限、菜单、会员与额度摘要。
 */
@Service
public class AuthMeService {

    /** 平台用户商业上下文聚合服务 */
    private final UserMeContextService userMeContextService;

    /** 平台快照 → Admin VO 转换器 */
    private final AuthMeVoAssembler authMeVoAssembler;

    /** IAM 用户仓储 */
    private final IamUserRepository iamUserRepository;

    /** 权限点仓储 */
    private final PermissionRepository permissionRepository;

    /**
     * @param userMeContextService 用户上下文聚合服务
     * @param authMeVoAssembler    VO 转换器
     * @param iamUserRepository    IAM 用户仓储
     * @param permissionRepository 权限点仓储
     */
    public AuthMeService(UserMeContextService userMeContextService,
                         AuthMeVoAssembler authMeVoAssembler,
                         IamUserRepository iamUserRepository,
                         PermissionRepository permissionRepository) {
        this.userMeContextService = userMeContextService;
        this.authMeVoAssembler = authMeVoAssembler;
        this.iamUserRepository = iamUserRepository;
        this.permissionRepository = permissionRepository;
    }

    /**
     * 构建 Admin 端 /me 上下文。
     *
     * @return 含 RBAC 与菜单的完整响应
     */
    public AuthMeResponse buildMe() {
        AuthUser authUser = UserContext.get();
        if (authUser == null || authUser.getUserId() == null) {
            throw Errors.of(PlatformErrorCode.UNAUTHORIZED);
        }

        UserMeContext context = userMeContextService.buildForUserId(authUser.getUserId());
        List<String> roles = resolveRoles(authUser, context.getUser().getId());
        List<PermissionEntity> permissions = permissionRepository.listByUserId(context.getUser().getId());
        List<String> permissionCodes = permissions.stream()
                .map(this::effectiveCode)
                .distinct()
                .toList();
        Map<String, String> aliases = buildAliasMap(permissions);

        AuthMeResponse response = authMeVoAssembler.fromContext(context);
        response.setRoles(roles);
        response.setPermissions(permissionCodes);
        response.setPermissionAliases(aliases);
        response.setMenuTree(buildMenuTree(permissions));
        return response;
    }

    private List<String> resolveRoles(AuthUser authUser, Long userId) {
        if (authUser.getRoles() != null && !authUser.getRoles().isEmpty()) {
            return authUser.getRoles();
        }
        return iamUserRepository.listRoleCodes(userId);
    }

    private String effectiveCode(PermissionEntity permission) {
        return StringUtils.hasText(permission.getAliasCode())
                ? permission.getAliasCode() : permission.getPermissionCode();
    }

    private Map<String, String> buildAliasMap(List<PermissionEntity> permissions) {
        Map<String, String> aliases = new LinkedHashMap<>();
        for (PermissionEntity permission : permissions) {
            if (StringUtils.hasText(permission.getAliasCode())) {
                aliases.put(permission.getPermissionCode(), permission.getAliasCode());
            }
        }
        return aliases;
    }

    private List<AuthMeResponse.AuthMeMenuNode> buildMenuTree(List<PermissionEntity> permissions) {
        List<AuthMeResponse.AuthMeMenuNode> nodes = new ArrayList<>();
        for (PermissionEntity permission : permissions) {
            if (!"menu".equalsIgnoreCase(permission.getKind())) {
                continue;
            }
            AuthMeResponse.AuthMeMenuNode node = new AuthMeResponse.AuthMeMenuNode();
            node.setKey(permission.getPermissionCode());
            node.setTitle(permission.getPermissionName());
            node.setPath(permission.getPath());
            node.setIcon(permission.getIcon());
            node.setChildren(List.of());
            nodes.add(node);
        }
        return nodes;
    }
}
