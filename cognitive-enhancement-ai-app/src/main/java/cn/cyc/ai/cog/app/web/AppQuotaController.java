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
 * C 端额度摘要接口。
 */
@Tag(name = "App-额度", description = "当前账户额度摘要")
@RestController
@RequestMapping("/api/app/quota")
public class AppQuotaController {

    /** 当前用户上下文聚合服务 */
    private final AppMeService appMeService;

    /**
     * @param appMeService 用户上下文服务
     */
    public AppQuotaController(AppMeService appMeService) {
        this.appMeService = appMeService;
    }

    /**
     * 查询当前账户周期/赠送/充值剩余额度。
     *
     * @return 额度摘要
     */
    @Operation(summary = "当前额度")
    @SecurityRequirements
    @GetMapping("/me")
    public ApiResponse<AppMeResponse.AppMeQuota> me() {
        return ApiResponse.success(appMeService.buildMe().getQuota());
    }
}
