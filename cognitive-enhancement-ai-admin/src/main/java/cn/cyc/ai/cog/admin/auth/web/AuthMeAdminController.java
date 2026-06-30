package cn.cyc.ai.cog.admin.auth.web;

import cn.cyc.ai.cog.admin.auth.dto.AuthMeResponse;
import cn.cyc.ai.cog.admin.auth.service.AuthMeService;
import cn.cyc.ai.cog.api.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理后台鉴权上下文接口。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Tag(name = "鉴权-当前用户", description = "返回用户、权限、菜单、会员与额度摘要")
@RestController
@RequestMapping("/api/admin/auth")
public class AuthMeAdminController {

    /** 认证Me服务。 */
    private final AuthMeService authMeService;

    /**
     * 创建认证Me管理后台接口。
     *
     * @param authMeService 认证Me服务
     */
    public AuthMeAdminController(AuthMeService authMeService) {
        this.authMeService = authMeService;
    }

    /**
     * 执行me。
     * @return 执行结果
     */
    @Operation(summary = "当前用户上下文", description = "聚合 user/account/permissions/menuTree/membership/quota，驱动 CMS 菜单与按钮渲染。")
    @SecurityRequirements
    @GetMapping("/me")
    public ApiResponse<AuthMeResponse> me() {
        return ApiResponse.success(authMeService.buildMe());
    }
}
