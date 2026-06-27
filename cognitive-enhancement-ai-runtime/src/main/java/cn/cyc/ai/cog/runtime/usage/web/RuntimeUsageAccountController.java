package cn.cyc.ai.cog.runtime.usage.web;

import cn.cyc.ai.cog.api.response.ApiResponse;
import cn.cyc.ai.cog.runtime.support.RuntimeResponses;
import cn.cyc.ai.cog.runtime.usage.domain.UsageAccount;
import cn.cyc.ai.cog.runtime.usage.spi.RuntimeUsageAccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Runtime 用量额度账户查询接口。
 *
 * @author cyc
 */
@Tag(name = "Runtime - 用量额度", description = "租户额度账户查询")
@RestController
@RequestMapping("/api/runtime/usage")
public class RuntimeUsageAccountController {

    /**
     * 用量额度账户服务。
     */
    private final RuntimeUsageAccountService runtimeUsageAccountService;

    /**
     * 构造用量额度账户查询接口。
     *
     * @param runtimeUsageAccountService 用量额度账户服务
     */
    public RuntimeUsageAccountController(RuntimeUsageAccountService runtimeUsageAccountService) {
        this.runtimeUsageAccountService = runtimeUsageAccountService;
    }

    /**
     * 查询当前租户额度账户。
     *
     * @return 当前租户额度账户
     */
    @Operation(summary = "查询当前租户额度账户", description = "返回余额、预检成本等额度账户信息。")
    @GetMapping("/account")
    public ApiResponse<UsageAccount> currentAccount() {
        return RuntimeResponses.success(runtimeUsageAccountService.currentAccount());
    }
}
