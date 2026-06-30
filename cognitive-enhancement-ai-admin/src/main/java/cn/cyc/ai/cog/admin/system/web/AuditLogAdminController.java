package cn.cyc.ai.cog.admin.system.web;

import cn.cyc.ai.cog.admin.security.RequirePermission;
import cn.cyc.ai.cog.admin.system.dto.AuditLogVO;
import cn.cyc.ai.cog.api.response.ApiResponse;
import cn.cyc.ai.cog.common.page.PageResult;
import cn.cyc.ai.cog.platform.system.domain.AuditLog;
import cn.cyc.ai.cog.platform.system.dto.AuditLogPageQuery;
import cn.cyc.ai.cog.platform.system.service.AuditLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * AuditLog管理后台接口
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Tag(name = "系统-审计日志", description = "管理后台操作审计")
@RestController
@RequestMapping("/api/admin/system/audit-logs")
public class AuditLogAdminController {

    /** auditLog服务。 */
    private final AuditLogService auditLogService;

    /**
     * 创建AuditLog管理后台接口。
     *
     * @param auditLogService auditLog服务
     */
    public AuditLogAdminController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    /**
     * 执行分页。
     *
     * @param query 查询
     * @return 执行结果
     */
    @Operation(summary = "审计日志分页")
    @RequirePermission("admin:dict:read")
    @PostMapping("/page")
    public ApiResponse<PageResult<AuditLogVO>> page(@RequestBody AuditLogPageQuery query) {
        PageResult<AuditLog> page = auditLogService.page(query);
        return ApiResponse.success(page.map(this::toVo));
    }

    /**
     * 转换为Vo。
     *
     * @param log 日志记录器
     * @return 转换结果
     */
    private AuditLogVO toVo(AuditLog log) {
        AuditLogVO vo = new AuditLogVO();
        vo.setId(log.id());
        vo.setTenantId(log.tenantId());
        vo.setOperatorId(log.operatorId());
        vo.setOperatorName(log.operatorName());
        vo.setAction(log.action());
        vo.setMessage(log.message());
        vo.setResourceType(log.resourceType());
        vo.setResourceId(log.resourceId());
        vo.setBeforeJson(log.beforeJson());
        vo.setAfterJson(log.afterJson());
        vo.setIpAddress(log.ipAddress());
        vo.setCreateTime(log.createTime());
        return vo;
    }
}
