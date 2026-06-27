package cn.cyc.ai.cog.admin.membership.web;

import cn.cyc.ai.cog.admin.membership.dto.AccountMembershipVO;
import cn.cyc.ai.cog.admin.security.RequirePermission;
import cn.cyc.ai.cog.api.response.ApiResponse;
import cn.cyc.ai.cog.common.page.PageResult;
import cn.cyc.ai.cog.platform.membership.domain.AccountMembership;
import cn.cyc.ai.cog.platform.membership.dto.MemberLevelRequest;
import cn.cyc.ai.cog.platform.membership.dto.MemberPageQuery;
import cn.cyc.ai.cog.platform.membership.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "会员-会员管理", description = "会员查询、等级调整、状态启停")
@RestController
@RequestMapping("/api/admin/membership/members")
public class MemberAdminController {

    private final MemberService memberService;

    public MemberAdminController(MemberService memberService) {
        this.memberService = memberService;
    }

    @Operation(summary = "分页查询会员", description = "支持 accountId/levelCode 过滤。需要 admin:member:update 权限点。")
    @RequirePermission("admin:member:update")
    @PostMapping("/page")
    public ApiResponse<PageResult<AccountMembershipVO>> page(@RequestBody MemberPageQuery query) {
        return ApiResponse.success(memberService.page(query).map(this::toVo));
    }

    @Operation(summary = "调整会员等级", description = "设置等级与到期时间。需要 admin:member:update 权限点。")
    @RequirePermission("admin:member:update")
    @PostMapping("/level")
    public ApiResponse<AccountMembershipVO> updateLevel(@Valid @RequestBody MemberLevelRequest request) {
        return ApiResponse.success(toVo(memberService.updateLevel(request.getId(), request)));
    }

    private AccountMembershipVO toVo(AccountMembership membership) {
        AccountMembershipVO vo = new AccountMembershipVO();
        vo.setId(membership.id());
        vo.setTenantId(membership.tenantId());
        vo.setAccountId(membership.accountId());
        vo.setLevelId(membership.levelId());
        vo.setLevelCode(membership.levelCode());
        vo.setExpireAt(membership.expireAt());
        vo.setSource(membership.source());
        return vo;
    }
}
