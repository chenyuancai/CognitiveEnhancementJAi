package cn.cyc.ai.cog.center.capability;

import cn.cyc.ai.cog.api.response.ApiResponse;
import cn.cyc.ai.cog.center.common.CenterPageResult;
import cn.cyc.ai.cog.center.support.CenterResponses;
import cn.cyc.ai.cog.core.metadata.capability.CapabilityReleasePointer;
import cn.cyc.ai.cog.core.metadata.capability.CapabilityTenantBinding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 能力后台管理接口。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Tag(name = "Center - Capability", description = "能力元数据与发布治理：版本、灰度、租户启停")
@RestController
@RequestMapping("/api/center/capabilities")
public class CapabilityAdminController {

    /** 日志记录器 */
    private static final Logger log = LoggerFactory.getLogger(CapabilityAdminController.class);

    /** 能力管理后台服务。 */
    private final CapabilityAdminService capabilityAdminService;
    /** 能力Release服务。 */
    private final CapabilityReleaseService capabilityReleaseService;

    /**
     * 创建能力管理后台接口。
     */
    public CapabilityAdminController(CapabilityAdminService capabilityAdminService,
                                     CapabilityReleaseService capabilityReleaseService) {
        this.capabilityAdminService = capabilityAdminService;
        this.capabilityReleaseService = capabilityReleaseService;
    }

    /**
     * 查询All列表。
     *
     * @param query 查询
     * @return 结果列表
     */
    @Operation(summary = "分页查询能力列表", description = "支持 capabilityCode/executeMode/riskLevel/status 等条件分页查询能力定义。")
    @PostMapping("/page")
    public ApiResponse<CenterPageResult<CapabilityResult>> listAll(@RequestBody CapabilityPageQuery query) {
        log.info("收到查询全部能力定义请求");
        return CenterResponses.success(capabilityAdminService.listPage(query));
    }

    /**
     * 获取人编码。
     *
     * @param code 编码
     * @return 人编码
     */
    @Operation(summary = "查询能力详情", description = "按 capabilityCode 返回能力定义。")
    @GetMapping("/{code}")
    public ApiResponse<CapabilityResult> getByCode(@PathVariable("code") String code) {
        log.info("收到查询能力定义详情请求，code={}", code);
        return CenterResponses.success(capabilityAdminService.getByCode(code));
    }

    /**
     * 创建Item。
     *
     * @param request 请求
     * @return 创建结果
     */
    @Operation(summary = "创建能力", description = "新建能力定义；capabilityCode 唯一，创建后不可修改。")
    @PostMapping
    public ApiResponse<CapabilityResult> create(@RequestBody CapabilityUpsertRequest request) {
        log.info("收到创建能力定义请求，capabilityCode={}", request.capabilityCode());
        return CenterResponses.success(capabilityAdminService.create(request));
    }

    /**
     * 更新Item。
     *
     * @param request 请求
     * @return 更新结果
     */
    @Operation(summary = "更新能力", description = "按 capabilityCode 更新能力定义。")
    @PostMapping("/update")
    public ApiResponse<CapabilityResult> update(@RequestBody CapabilityUpsertRequest request) {
        log.info("收到更新能力定义请求，code={}", request.capabilityCode());
        return CenterResponses.success(capabilityAdminService.update(request.capabilityCode(), request));
    }

    /**
     * 查询Versions列表。
     *
     * @param code 编码
     * @return 结果列表
     */
    @Operation(summary = "查询能力版本列表", description = "返回同一 capabilityCode 下的全部版本。")
    @GetMapping("/{code}/versions")
    public ApiResponse<List<CapabilityResult>> listVersions(@PathVariable("code") String code) {
        log.info("收到查询 Capability 版本列表请求，code={}", code);
        return CenterResponses.success(capabilityReleaseService.listVersions(code));
    }

    /**
     * 创建Draft。
     *
     * @param request 请求
     * @return 创建结果
     */
    @Operation(summary = "创建能力草稿版本", description = "基于已发布版本创建新的草稿版本。")
    @PostMapping("/drafts")
    public ApiResponse<CapabilityResult> createDraft(@RequestBody CapabilityDraftRequest request) {
        log.info("收到创建 Capability 草稿请求，code={}", request.capabilityCode());
        return CenterResponses.success(capabilityReleaseService.createDraft(request.capabilityCode(), request));
    }

    /**
     * 执行publish。
     *
     * @param request 请求
     * @return 执行结果
     */
    @Operation(summary = "发布能力版本", description = "将指定版本设为已发布，供 Runtime 解析。")
    @PostMapping("/publish")
    public ApiResponse<CapabilityResult> publish(@RequestBody CapabilityPublishRequest request) {
        log.info("收到发布 Capability 请求，code={}, version={}", request.capabilityCode(), request.version());
        return CenterResponses.success(capabilityReleaseService.publish(request.capabilityCode(), request));
    }

    /**
     * 执行configureGray。
     *
     * @param request 请求
     * @return 执行结果
     */
    @Operation(summary = "配置能力灰度", description = "设置灰度版本与流量比例。")
    @PostMapping("/gray")
    public ApiResponse<CapabilityReleasePointer> configureGray(@RequestBody CapabilityGrayRequest request) {
        log.info("收到配置 Capability 灰度请求，code={}", request.capabilityCode());
        return CenterResponses.success(capabilityReleaseService.configureGray(request.capabilityCode(), request));
    }

    /**
     * 执行configure租户。
     *
     * @param request 请求
     * @return 执行结果
     */
    @Operation(summary = "配置租户能力启停", description = "按租户启用或停用指定能力。")
    @PostMapping("/tenants/configure")
    public ApiResponse<CapabilityTenantBinding> configureTenant(@RequestBody CapabilityTenantBindingRequest request) {
        log.info("收到配置 Capability 租户启停请求，code={}, tenantCode={}", request.capabilityCode(), request.tenantCode());
        return CenterResponses.success(capabilityReleaseService.configureTenant(request.capabilityCode(), request.tenantCode(), request));
    }
}
