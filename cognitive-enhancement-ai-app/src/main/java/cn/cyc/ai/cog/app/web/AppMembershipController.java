package cn.cyc.ai.cog.app.web;

import cn.cyc.ai.cog.app.dto.AppMeResponse;
import cn.cyc.ai.cog.app.service.AppMeService;
import cn.cyc.ai.cog.api.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * C 端会员接口：当前账户会员信息。
 */
@Tag(name = "App-会员", description = "当前账户会员信息")
@RestController
@RequestMapping("/api/app/membership")
public class AppMembershipController {

    /** 当前用户上下文聚合服务 */
    private final AppMeService appMeService;

    /**
     * @param appMeService 用户上下文服务
     */
    public AppMembershipController(AppMeService appMeService) {
        this.appMeService = appMeService;
    }

    /**
     * 查询当前账户会员等级与到期时间。
     *
     * @return 会员摘要
     */
    @Operation(summary = "当前会员")
    @SecurityRequirements
    @GetMapping("/me")
    public ApiResponse<AppMeResponse.AppMeMembership> me() {
        return ApiResponse.success(appMeService.buildMe().getMembership());
    }
}
