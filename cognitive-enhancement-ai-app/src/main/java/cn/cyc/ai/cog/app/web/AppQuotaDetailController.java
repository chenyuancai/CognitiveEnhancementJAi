package cn.cyc.ai.cog.app.web;

import cn.cyc.ai.cog.platform.account.service.AccountContextResolver;
import cn.cyc.ai.cog.api.response.ApiResponse;
import cn.cyc.ai.cog.common.context.UserContext;
import cn.cyc.ai.cog.common.page.PageQuery;
import cn.cyc.ai.cog.common.page.PageResult;
import cn.cyc.ai.cog.platform.quota.domain.QuotaMemberAlloc;
import cn.cyc.ai.cog.platform.quota.domain.TokenRecord;
import cn.cyc.ai.cog.platform.quota.service.QuotaMemberAllocService;
import cn.cyc.ai.cog.platform.quota.service.QuotaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * C 端额度明细接口：成员分配与 Token 流水。
 */
@Tag(name = "App-额度明细", description = "成员分配与 Token 流水")
@RestController
@RequestMapping("/api/app/quota")
public class AppQuotaDetailController {

    /** 当前账户解析器 */
    private final AccountContextResolver accountContextResolver;

    /** 成员额度分配服务 */
    private final QuotaMemberAllocService quotaMemberAllocService;

    /** 额度与流水服务 */
    private final QuotaService quotaService;

    /**
     * @param accountContextResolver   账户上下文解析
     * @param quotaMemberAllocService  成员分配服务
     * @param quotaService             额度服务
     */
    public AppQuotaDetailController(AccountContextResolver accountContextResolver,
                                    QuotaMemberAllocService quotaMemberAllocService,
                                    QuotaService quotaService) {
        this.accountContextResolver = accountContextResolver;
        this.quotaMemberAllocService = quotaMemberAllocService;
        this.quotaService = quotaService;
    }

    /**
     * 查询当前用户在账户下的成员额度分配。
     *
     * @return 成员分配记录
     */
    @Operation(summary = "我的成员额度分配")
    @SecurityRequirements
    @GetMapping("/member-alloc/me")
    public ApiResponse<QuotaMemberAlloc> myMemberAlloc() {
        Long accountId = accountContextResolver.resolveCurrentAccountId();
        Long userId = UserContext.currentUserId();
        return ApiResponse.success(quotaMemberAllocService.getByAccountAndUser(accountId, userId));
    }

    /**
     * 分页查询当前账户 Token 消耗流水。
     *
     * @param query 分页参数
     * @return Token 流水分页
     */
    @Operation(summary = "我的 Token 流水")
    @SecurityRequirements
    @PostMapping("/token-records/me/page")
    public ApiResponse<PageResult<TokenRecord>> myTokenRecords(
            @RequestBody(required = false) PageQuery query) {
        PageQuery body = query == null ? new PageQuery() : query;
        Long accountId = accountContextResolver.resolveCurrentAccountId();
        return ApiResponse.success(quotaService.pageTokenRecords(body.getCurrent(), body.getSize(), accountId));
    }
}
