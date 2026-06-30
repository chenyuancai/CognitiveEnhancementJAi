package cn.cyc.ai.cog.admin.membership.web;

import cn.cyc.ai.cog.admin.membership.dto.AccountMembershipVO;
import cn.cyc.ai.cog.admin.membership.dto.MembershipChangeLogVO;
import cn.cyc.ai.cog.admin.membership.dto.MembershipLevelVO;
import cn.cyc.ai.cog.admin.membership.dto.MembershipChangeLogPageQuery;
import cn.cyc.ai.cog.admin.security.RequirePermission;
import cn.cyc.ai.cog.api.response.ApiResponse;
import cn.cyc.ai.cog.common.page.PageResult;
import cn.cyc.ai.cog.platform.membership.domain.AccountMembership;
import cn.cyc.ai.cog.platform.membership.domain.MembershipChangeLog;
import cn.cyc.ai.cog.platform.membership.domain.MembershipLevel;
import cn.cyc.ai.cog.platform.membership.dto.GrantMembershipRequest;
import cn.cyc.ai.cog.platform.membership.dto.MembershipLevelPageQuery;
import cn.cyc.ai.cog.platform.membership.dto.MembershipLevelSaveRequest;
import cn.cyc.ai.cog.platform.membership.service.MembershipLevelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 会员等级管理后台接口
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Tag(name = "会员-等级目录", description = "会员等级 CRUD、手动授予、变更审计")
@RestController
@RequestMapping("/api/admin/membership/levels")
public class MembershipLevelAdminController {

    /** 会员等级服务。 */
    private final MembershipLevelService membershipLevelService;

    /**
     * 创建会员等级管理后台接口。
     *
     * @param membershipLevelService 会员等级服务
     */
    public MembershipLevelAdminController(MembershipLevelService membershipLevelService) {
        this.membershipLevelService = membershipLevelService;
    }

    /**
     * 执行分页。
     *
     * @param query 查询
     * @return 执行结果
     */
    @Operation(summary = "分页查询会员等级")
    @RequirePermission("admin:member:update")
    @PostMapping("/page")
    public ApiResponse<PageResult<MembershipLevelVO>> page(@RequestBody MembershipLevelPageQuery query) {
        return ApiResponse.success(membershipLevelService.page(query).map(this::toLevelVo));
    }

    /**
     * 查询All列表。
     *
     * @param segment segment
     * @return 结果列表
     */
    @Operation(summary = "全部会员等级（下拉）")
    @RequirePermission("admin:member:update")
    @GetMapping("/all")
    public ApiResponse<List<MembershipLevelVO>> listAll(@RequestParam(required = false) String segment) {
        return ApiResponse.success(membershipLevelService.listAll(segment).stream().map(this::toLevelVo).toList());
    }

    /**
     * 执行detail。
     *
     * @param id 主键 ID
     * @return 执行结果
     */
    @Operation(summary = "等级详情")
    @RequirePermission("admin:member:update")
    @GetMapping("/{id}")
    public ApiResponse<MembershipLevelVO> detail(@PathVariable Long id) {
        return ApiResponse.success(toLevelVo(membershipLevelService.detail(id)));
    }

    /**
     * 创建Item。
     *
     * @param request 请求
     * @return 创建结果
     */
    @Operation(summary = "新增等级")
    @RequirePermission("admin:member:update")
    @PostMapping
    public ApiResponse<MembershipLevelVO> create(@Valid @RequestBody MembershipLevelSaveRequest request) {
        return ApiResponse.success(toLevelVo(membershipLevelService.create(request)));
    }

    /**
     * 更新Item。
     *
     * @param request 请求
     * @return 更新结果
     */
    @Operation(summary = "编辑等级")
    @RequirePermission("admin:member:update")
    @PostMapping("/update")
    public ApiResponse<MembershipLevelVO> update(@Valid @RequestBody MembershipLevelSaveRequest request) {
        return ApiResponse.success(toLevelVo(membershipLevelService.update(request.getId(), request)));
    }

    /**
     * 执行grant。
     *
     * @param request 请求
     * @return 执行结果
     */
    @Operation(summary = "手动授予会员")
    @RequirePermission("admin:member:grant")
    @PostMapping("/grant")
    public ApiResponse<AccountMembershipVO> grant(@Valid @RequestBody GrantMembershipRequest request) {
        return ApiResponse.success(toMembershipVo(membershipLevelService.grant(request)));
    }

    /**
     * 执行changeLogs。
     *
     * @param query 查询
     * @return 执行结果
     */
    @Operation(summary = "会员变更审计")
    @RequirePermission("admin:member:update")
    @PostMapping("/change-logs/page")
    public ApiResponse<PageResult<MembershipChangeLogVO>> changeLogs(@RequestBody MembershipChangeLogPageQuery query) {
        return ApiResponse.success(membershipLevelService.pageChangeLogs(
                query.getCurrent(), query.getSize(), query.getAccountId()).map(this::toLogVo));
    }

    /**
     * 转换为等级Vo。
     *
     * @param level 等级
     * @return 转换结果
     */
    private MembershipLevelVO toLevelVo(MembershipLevel level) {
        MembershipLevelVO vo = new MembershipLevelVO();
        vo.setId(level.id());
        vo.setLevelCode(level.levelCode());
        vo.setLevelName(level.levelName());
        vo.setSegment(level.segment());
        vo.setIsDefault(level.isDefault());
        vo.setSortNo(level.sortNo());
        vo.setStatus(level.status());
        vo.setBenefitsJson(level.benefitsJson());
        return vo;
    }

    /**
     * 转换为会员Vo。
     *
     * @param membership 会员
     * @return 转换结果
     */
    private AccountMembershipVO toMembershipVo(AccountMembership membership) {
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

    /**
     * 转换为LogVo。
     *
     * @param log 日志记录器
     * @return 转换结果
     */
    private MembershipChangeLogVO toLogVo(MembershipChangeLog log) {
        MembershipChangeLogVO vo = new MembershipChangeLogVO();
        vo.setId(log.id());
        vo.setTenantId(log.tenantId());
        vo.setAccountId(log.accountId());
        vo.setFromLevelCode(log.fromLevelCode());
        vo.setToLevelCode(log.toLevelCode());
        vo.setChangeType(log.changeType());
        vo.setOperatorId(log.operatorId());
        vo.setMessage(log.message());
        vo.setRemark(log.remark());
        vo.setCreateTime(log.createTime());
        return vo;
    }
}
