package cn.cyc.ai.cog.admin.quota.web;

import cn.cyc.ai.cog.admin.quota.dto.QuotaMemberAllocVO;
import cn.cyc.ai.cog.admin.security.RequirePermission;
import cn.cyc.ai.cog.api.response.ApiResponse;
import cn.cyc.ai.cog.platform.quota.domain.QuotaMemberAlloc;
import cn.cyc.ai.cog.platform.quota.dto.QuotaMemberAllocSaveRequest;
import cn.cyc.ai.cog.platform.quota.service.QuotaMemberAllocService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "计费-成员额度", description = "组织共享池下的成员分配")
@RestController
@RequestMapping("/api/admin/quota/accounts/{accountId}/member-allocs")
public class QuotaMemberAllocAdminController {

    private final QuotaMemberAllocService quotaMemberAllocService;

    public QuotaMemberAllocAdminController(QuotaMemberAllocService quotaMemberAllocService) {
        this.quotaMemberAllocService = quotaMemberAllocService;
    }

    @Operation(summary = "成员额度分配列表")
    @RequirePermission("admin:order:update")
    @GetMapping
    public ApiResponse<List<QuotaMemberAllocVO>> list(@PathVariable Long accountId) {
        return ApiResponse.success(quotaMemberAllocService.listByAccount(accountId).stream().map(this::toVo).toList());
    }

    @Operation(summary = "设置成员额度")
    @RequirePermission("admin:order:update")
    @PostMapping
    public ApiResponse<QuotaMemberAllocVO> allocate(@PathVariable Long accountId,
                                                    @Valid @RequestBody QuotaMemberAllocSaveRequest request) {
        return ApiResponse.success(toVo(quotaMemberAllocService.allocate(accountId, request)));
    }

    @Operation(summary = "移除成员额度")
    @RequirePermission("admin:order:update")
    @DeleteMapping("/{userId}")
    public ApiResponse<Void> remove(@PathVariable Long accountId, @PathVariable Long userId) {
        quotaMemberAllocService.remove(accountId, userId);
        return ApiResponse.success(null);
    }

    private QuotaMemberAllocVO toVo(QuotaMemberAlloc alloc) {
        QuotaMemberAllocVO vo = new QuotaMemberAllocVO();
        vo.setId(alloc.id());
        vo.setAccountId(alloc.accountId());
        vo.setUserId(alloc.userId());
        vo.setAllocatedAmount(alloc.allocatedAmount());
        vo.setUsedAmount(alloc.usedAmount());
        return vo;
    }
}
