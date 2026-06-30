package cn.cyc.ai.cog.admin.system.web;

import cn.cyc.ai.cog.admin.security.RequirePermission;
import cn.cyc.ai.cog.admin.system.dto.SecurityConfigVO;
import cn.cyc.ai.cog.api.response.ApiResponse;
import cn.cyc.ai.cog.common.page.PageResult;
import cn.cyc.ai.cog.platform.system.domain.SecurityConfig;
import cn.cyc.ai.cog.platform.system.dto.SecurityConfigPageQuery;
import cn.cyc.ai.cog.platform.system.dto.SecurityConfigSaveRequest;
import cn.cyc.ai.cog.platform.system.service.SecurityConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Security配置管理后台接口
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Tag(name = "系统-安全配置", description = "密码策略、登录锁定等")
@RestController
@RequestMapping("/api/admin/system/security-configs")
public class SecurityConfigAdminController {

    /** security配置服务。 */
    private final SecurityConfigService securityConfigService;

    /**
     * 创建Security配置管理后台接口。
     *
     * @param securityConfigService security配置服务
     */
    public SecurityConfigAdminController(SecurityConfigService securityConfigService) {
        this.securityConfigService = securityConfigService;
    }

    /**
     * 执行分页。
     *
     * @param query 查询
     * @return 执行结果
     */
    @Operation(summary = "安全配置分页")
    @RequirePermission("admin:dict:read")
    @PostMapping("/page")
    public ApiResponse<PageResult<SecurityConfigVO>> page(@RequestBody SecurityConfigPageQuery query) {
        PageResult<SecurityConfig> page = securityConfigService.page(query);
        return ApiResponse.success(page.map(this::toVo));
    }

    /**
     * 创建Item。
     *
     * @param request 请求
     * @return 创建结果
     */
    @Operation(summary = "新增配置")
    @RequirePermission("admin:dict:update")
    @PostMapping
    public ApiResponse<SecurityConfigVO> create(@Valid @RequestBody SecurityConfigSaveRequest request) {
        return ApiResponse.success(toVo(securityConfigService.save(null, request)));
    }

    /**
     * 更新Item。
     *
     * @param request 请求
     * @return 更新结果
     */
    @Operation(summary = "编辑配置")
    @RequirePermission("admin:dict:update")
    @PostMapping("/update")
    public ApiResponse<SecurityConfigVO> update(@Valid @RequestBody SecurityConfigSaveRequest request) {
        return ApiResponse.success(toVo(securityConfigService.save(request.getId(), request)));
    }

    /**
     * 转换为Vo。
     *
     * @param config 配置
     * @return 转换结果
     */
    private SecurityConfigVO toVo(SecurityConfig config) {
        SecurityConfigVO vo = new SecurityConfigVO();
        vo.setId(config.id());
        vo.setConfigKey(config.configKey());
        vo.setConfigValue(config.configValue());
        vo.setDescription(config.description());
        return vo;
    }
}
