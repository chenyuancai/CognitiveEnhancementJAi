package cn.cyc.ai.cog.app.web;

import cn.cyc.ai.cog.app.dto.AppMeResponse;
import cn.cyc.ai.cog.app.dto.AppRegisterResponse;
import cn.cyc.ai.cog.app.service.AppMeService;
import cn.cyc.ai.cog.api.response.ApiResponse;
import cn.cyc.ai.cog.platform.iam.domain.IamUser;
import cn.cyc.ai.cog.platform.iam.dto.UserRegisterRequest;
import cn.cyc.ai.cog.platform.iam.service.IamAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * C 端鉴权接口：当前用户上下文。
 */
@Tag(name = "App-鉴权", description = "C 端当前用户上下文")
@RestController
@RequestMapping("/api/app/auth")
public class AppAuthController {

    /** 当前用户上下文聚合服务 */
    private final AppMeService appMeService;

    /** IAM 认证服务 */
    private final IamAuthService iamAuthService;

    /**
     * @param appMeService   用户上下文服务
     * @param iamAuthService IAM 认证服务
     */
    public AppAuthController(AppMeService appMeService, IamAuthService iamAuthService) {
        this.appMeService = appMeService;
        this.iamAuthService = iamAuthService;
    }

    /**
     * C 端用户注册（受安全配置开关控制）。
     *
     * @param request 注册请求
     * @return 注册结果
     */
    @Operation(summary = "用户注册", description = "支持 USERNAME/PHONE/EMAIL 三种方式，由安全配置开关控制。")
    @SecurityRequirements
    @PostMapping("/register")
    public ApiResponse<AppRegisterResponse> register(@Valid @RequestBody UserRegisterRequest request) {
        IamUser user = iamAuthService.register(request);
        AppRegisterResponse response = new AppRegisterResponse();
        response.setUserId(user.id());
        response.setUsername(user.username());
        response.setNickname(user.nickname());
        return ApiResponse.success(response);
    }

    /**
     * 返回当前登录用户的账户、会员与额度摘要。
     *
     * @return 用户上下文响应
     */
    @Operation(summary = "当前用户上下文")
    @SecurityRequirements
    @GetMapping("/me")
    public ApiResponse<AppMeResponse> me() {
        return ApiResponse.success(appMeService.buildMe());
    }
}
