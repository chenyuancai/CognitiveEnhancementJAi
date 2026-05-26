package cn.cyc.ai.cog.center.capability;

import cn.cyc.ai.cog.api.response.ApiResponse;
import cn.cyc.ai.cog.center.support.CenterResponses;
import cn.cyc.ai.cog.center.support.DefinitionListResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 能力后台管理接口。
 *
 * @author cyc
 */
@RestController
@RequestMapping("/api/center/capabilities")
public class CapabilityAdminController {

    /**
     * 控制器日志。
     */
    private static final Logger log = LoggerFactory.getLogger(CapabilityAdminController.class);

    /**
     * 能力后台管理服务。
     */
    private final CapabilityAdminService capabilityAdminService;

    /**
     * 创建能力后台管理控制器。
     *
     * @param capabilityAdminService 能力后台管理服务
     */
    public CapabilityAdminController(CapabilityAdminService capabilityAdminService) {
        this.capabilityAdminService = capabilityAdminService;
    }

    /**
     * 查询全部能力定义。
     *
     * @return 能力定义列表
     */
    @GetMapping
    public ApiResponse<DefinitionListResult<CapabilityResult>> listAll() {
        log.info("收到查询全部能力定义请求");
        return CenterResponses.success(capabilityAdminService.listAll());
    }

    /**
     * 按编码查询能力定义详情。
     *
     * @param code 能力编码
     * @return 能力定义详情
     */
    @GetMapping("/{code}")
    public ApiResponse<CapabilityResult> getByCode(@PathVariable("code") String code) {
        log.info("收到查询能力定义详情请求，code={}", code);
        return CenterResponses.success(capabilityAdminService.getByCode(code));
    }

    /**
     * 创建能力定义。
     *
     * @param request 能力写入请求
     * @return 创建后的能力定义
     */
    @PostMapping
    public ApiResponse<CapabilityResult> create(@RequestBody CapabilityUpsertRequest request) {
        log.info("收到创建能力定义请求，capabilityCode={}", request.capabilityCode());
        return CenterResponses.success(capabilityAdminService.create(request));
    }

    /**
     * 更新能力定义。
     *
     * @param code    能力编码
     * @param request 能力写入请求
     * @return 更新后的能力定义
     */
    @PutMapping("/{code}")
    public ApiResponse<CapabilityResult> update(@PathVariable("code") String code, @RequestBody CapabilityUpsertRequest request) {
        log.info("收到更新能力定义请求，code={}", code);
        return CenterResponses.success(capabilityAdminService.update(code, request));
    }
}
