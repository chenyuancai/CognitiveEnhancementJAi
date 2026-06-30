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

/**
 * 额度MemberAlloc管理后台接口
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Tag(name = "计费-成员额度", description = "组织共享池下的成员分配")
@RestController
@RequestMapping("/api/admin/quota/accounts/{accountId}/member-allocs")
public class QuotaMemberAllocAdminController {

    /** 额度MemberAlloc服务。 */
    private final QuotaMemberAllocService quotaMemberAllocService;

    /**
     * 创建额度MemberAlloc管理后台接口。
     *
     * @param quotaMemberAllocService 额度MemberAlloc服务
     */
    public QuotaMemberAllocAdminController(QuotaMemberAllocService quotaMemberAllocService) {
        this.quotaMemberAllocService = quotaMemberAllocService;
    }

    /**
     * 查询Item列表。
     *
     * @param accountId 账户ID
     * @return 结果列表
     */
    @Operation(summary = "成员额度分配列表")
    @RequirePermission("admin:order:update")
    @GetMapping
    public ApiResponse<List<QuotaMemberAllocVO>> list(@PathVariable Long accountId) {
        return ApiResponse.success(quotaMemberAllocService.listByAccount(accountId).stream().map(this::toVo).toList());
    }

    /**
     * 执行allocate。
     * @return 执行结果
     */
    @Operation(summary = "设置成员额度")
    @RequirePermission("admin:order:update")
    @PostMapping
    public ApiResponse<QuotaMemberAllocVO> allocate(@PathVariable Long accountId,
                                                    @Valid @RequestBody QuotaMemberAllocSaveRequest request) {
        return ApiResponse.success(toVo(quotaMemberAllocService.allocate(accountId, request)));
    }

    /**
     * 删除Item。
     *
     * @param accountId 账户ID
     * @param userId 用户 ID
     */
    @Operation(summary = "移除成员额度")
    @RequirePermission("admin:order:update")
    @DeleteMapping("/{userId}")
    public ApiResponse<Void> remove(@PathVariable Long accountId, @PathVariable Long userId) {
        quotaMemberAllocService.remove(accountId, userId);
        return ApiResponse.success(null);
    }

    /**
     * 转换为Vo。
     *
     * @param alloc alloc
     * @return 转换结果
     */
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
