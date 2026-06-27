package cn.cyc.ai.cog.admin.quota.web;

import cn.cyc.ai.cog.admin.quota.dto.QuotaAccountVO;
import cn.cyc.ai.cog.admin.quota.dto.TokenRecordPageQuery;
import cn.cyc.ai.cog.admin.quota.dto.TokenRecordVO;
import cn.cyc.ai.cog.admin.security.RequirePermission;
import cn.cyc.ai.cog.api.response.ApiResponse;
import cn.cyc.ai.cog.common.page.PageResult;
import cn.cyc.ai.cog.platform.quota.domain.QuotaAccount;
import cn.cyc.ai.cog.platform.quota.domain.TokenRecord;
import cn.cyc.ai.cog.platform.quota.dto.QuotaAdjustRequest;
import cn.cyc.ai.cog.platform.quota.service.QuotaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "计费-额度", description = "额度账户、Token 流水、手动调整")
@RestController
@RequestMapping("/api/admin/quota")
public class QuotaAdminController {

    private final QuotaService quotaService;

    public QuotaAdminController(QuotaService quotaService) {
        this.quotaService = quotaService;
    }

    @Operation(summary = "查询账户额度")
    @RequirePermission("admin:order:update")
    @GetMapping("/accounts/{accountId}")
    public ApiResponse<QuotaAccountVO> account(@PathVariable Long accountId) {
        return ApiResponse.success(toVo(quotaService.getByAccountId(accountId)));
    }

    @Operation(summary = "手动调整额度")
    @RequirePermission("admin:order:update")
    @PostMapping("/accounts/adjust")
    public ApiResponse<QuotaAccountVO> adjust(@Valid @RequestBody QuotaAdjustRequest request) {
        return ApiResponse.success(toVo(quotaService.adjust(request.getAccountId(), request.getBucket(), request.getDeltaAmount(), request.getRemark())));
    }

    @Operation(summary = "Token 流水分页")
    @RequirePermission("admin:order:update")
    @PostMapping("/token-records/page")
    public ApiResponse<PageResult<TokenRecordVO>> tokenRecords(@RequestBody TokenRecordPageQuery query) {
        return ApiResponse.success(quotaService.pageTokenRecords(
                query.getCurrent(), query.getSize(), query.getAccountId()).map(this::toRecordVo));
    }

    private QuotaAccountVO toVo(QuotaAccount quota) {
        QuotaAccountVO vo = new QuotaAccountVO();
        vo.setId(quota.id());
        vo.setTenantId(quota.tenantId());
        vo.setAccountId(quota.accountId());
        vo.setCycleRemaining(quota.cycleRemaining());
        vo.setCycleTotal(quota.cycleTotal());
        vo.setCycleResetAt(quota.cycleResetAt());
        vo.setGiftRemaining(quota.giftRemaining());
        vo.setGiftTotal(quota.giftTotal());
        vo.setTopupRemaining(quota.topupRemaining());
        vo.setTopupTotal(quota.topupTotal());
        return vo;
    }

    private TokenRecordVO toRecordVo(TokenRecord record) {
        TokenRecordVO vo = new TokenRecordVO();
        vo.setId(record.id());
        vo.setTenantId(record.tenantId());
        vo.setAccountId(record.accountId());
        vo.setMemberUserId(record.memberUserId());
        vo.setRecordType(record.recordType());
        vo.setBucket(record.bucket());
        vo.setDeltaAmount(record.deltaAmount());
        vo.setBalanceAfter(record.balanceAfter());
        vo.setBizType(record.bizType());
        vo.setBizId(record.bizId());
        vo.setIdempotencyKey(record.idempotencyKey());
        vo.setMessage(record.message());
        vo.setCreateTime(record.createTime());
        return vo;
    }
}
