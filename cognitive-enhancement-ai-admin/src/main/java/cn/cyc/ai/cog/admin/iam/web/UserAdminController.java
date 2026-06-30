package cn.cyc.ai.cog.admin.iam.web;

import cn.cyc.ai.cog.admin.iam.dto.UserAdminVO;
import cn.cyc.ai.cog.admin.security.RequirePermission;
import cn.cyc.ai.cog.api.response.ApiResponse;
import cn.cyc.ai.cog.common.page.PageResult;
import cn.cyc.ai.cog.platform.iam.dto.UserAdminResult;
import cn.cyc.ai.cog.platform.iam.dto.UserPageQuery;
import cn.cyc.ai.cog.platform.iam.dto.UserStatusUpdateRequest;
import cn.cyc.ai.cog.platform.iam.service.UserAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户管理后台接口
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Tag(name = "账号治理-用户管理", description = "用户分页、详情、状态管理")
@RestController
@RequestMapping("/api/admin/users")
public class UserAdminController {

    /** 用户管理后台服务。 */
    private final UserAdminService userAdminService;

    /**
     * 创建用户管理后台接口。
     *
     * @param userAdminService 用户管理后台服务
     */
    public UserAdminController(UserAdminService userAdminService) {
        this.userAdminService = userAdminService;
    }

    /**
     * 执行分页。
     *
     * @param query 查询
     * @return 执行结果
     */
    @Operation(summary = "分页查询用户", description = "默认返回全部用户；可选 userType=ADMIN|CUSTOMER、keyword、status 过滤。")
    @RequirePermission("admin:user:view")
    @PostMapping("/page")
    public ApiResponse<PageResult<UserAdminVO>> page(@RequestBody UserPageQuery query) {
        return ApiResponse.success(userAdminService.page(query).map(this::toVo));
    }

    /**
     * 执行detail。
     *
     * @param id 主键 ID
     * @return 执行结果
     */
    @Operation(summary = "用户详情")
    @RequirePermission("admin:user:view")
    @GetMapping("/{id}")
    public ApiResponse<UserAdminVO> detail(@PathVariable Long id) {
        return ApiResponse.success(toVo(userAdminService.detail(id)));
    }

    /**
     * 更新状态。
     *
     * @param request 请求
     * @return 更新结果
     */
    @Operation(summary = "更新用户状态")
    @RequirePermission("admin:user:update")
    @PostMapping("/status")
    public ApiResponse<UserAdminVO> updateStatus(@RequestBody UserStatusUpdateRequest request) {
        return ApiResponse.success(toVo(userAdminService.updateStatus(request.getId(), request)));
    }

    /**
     * 转换为Vo。
     *
     * @param result 结果
     * @return 转换结果
     */
    private UserAdminVO toVo(UserAdminResult result) {
        UserAdminVO vo = new UserAdminVO();
        vo.setId(result.getId());
        vo.setUsername(result.getUsername());
        vo.setNickname(result.getNickname());
        vo.setEmail(result.getEmail());
        vo.setPhone(result.getPhone());
        vo.setAvatarUrl(result.getAvatarUrl());
        vo.setStatus(result.getStatus());
        vo.setUserType(result.getUserType());
        vo.setLevelCode(result.getLevelCode());
        vo.setAccountId(result.getAccountId());
        vo.setTenantId(result.getTenantId());
        return vo;
    }
}
