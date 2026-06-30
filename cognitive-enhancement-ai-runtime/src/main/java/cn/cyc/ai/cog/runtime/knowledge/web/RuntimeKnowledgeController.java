package cn.cyc.ai.cog.runtime.knowledge.web;

import cn.cyc.ai.cog.api.response.ApiResponse;
import cn.cyc.ai.cog.runtime.api.RuntimeListResult;
import cn.cyc.ai.cog.runtime.knowledge.domain.KnowledgeFragment;
import cn.cyc.ai.cog.runtime.knowledge.domain.ScenarioKnowledgeBinding;
import cn.cyc.ai.cog.runtime.knowledge.dto.CreateKnowledgeFragmentRequest;
import cn.cyc.ai.cog.runtime.knowledge.dto.CreateScenarioBindingRequest;
import cn.cyc.ai.cog.runtime.knowledge.dto.KnowledgeRetrievalResult;
import cn.cyc.ai.cog.runtime.knowledge.service.KnowledgeBindingService;
import cn.cyc.ai.cog.runtime.knowledge.service.KnowledgeFragmentService;
import cn.cyc.ai.cog.runtime.knowledge.service.KnowledgeRetrievalService;
import cn.cyc.ai.cog.runtime.support.RuntimeResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Runtime 知识管理接口。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Tag(name = "Runtime - 知识库", description = "知识片段、场景绑定与检索")
@RestController
@RequestMapping("/api/runtime/knowledge")
public class RuntimeKnowledgeController {

    /**
     * 控制器日志。
     */
    private static final Logger log = LoggerFactory.getLogger(RuntimeKnowledgeController.class);

    /**
     * 知识片段服务。
     */
    private final KnowledgeFragmentService knowledgeFragmentService;

    /**
     * 场景知识绑定服务。
     */
    private final KnowledgeBindingService knowledgeBindingService;

    /**
     * 知识检索服务。
     */
    private final KnowledgeRetrievalService knowledgeRetrievalService;

    /**
     * 构造 Runtime 知识管理接口。
     *
     * @param knowledgeFragmentService  知识片段服务
     * @param knowledgeBindingService   场景知识绑定服务
     * @param knowledgeRetrievalService 知识检索服务
     */
    public RuntimeKnowledgeController(KnowledgeFragmentService knowledgeFragmentService,
                                      KnowledgeBindingService knowledgeBindingService,
                                      KnowledgeRetrievalService knowledgeRetrievalService) {
        this.knowledgeFragmentService = knowledgeFragmentService;
        this.knowledgeBindingService = knowledgeBindingService;
        this.knowledgeRetrievalService = knowledgeRetrievalService;
    }

    /**
     * 创建知识片段。
     *
     * @param request 创建知识片段请求
     * @return 新建知识片段
     */
    @Operation(summary = "创建知识片段", description = "写入知识库片段内容。")
    @PostMapping("/fragments")
    public ApiResponse<KnowledgeFragment> createFragment(@RequestBody CreateKnowledgeFragmentRequest request) {
        log.info("收到创建知识片段请求, knowledgeCode={}", request.knowledgeCode());
        return RuntimeResponses.success(knowledgeFragmentService.createFragment(
                request.knowledgeCode(),
                request.title(),
                request.content(),
                request.tags(),
                request.status()));
    }

    /**
     * 查询知识片段列表。
     *
     * @param knowledgeCode 知识库编码筛选条件
     * @return 知识片段列表
     */
    @Operation(summary = "查询知识片段列表", description = "分页查询知识片段。")
    @GetMapping("/fragments")
    public ApiResponse<RuntimeListResult<KnowledgeFragment>> listFragments(
            @RequestParam(name = "knowledgeCode", required = false) String knowledgeCode) {
        log.info("收到知识片段查询请求, knowledgeCode={}", knowledgeCode);
        var fragments = knowledgeFragmentService.listFragments(knowledgeCode);
        return RuntimeResponses.success(new RuntimeListResult<>(fragments.size(), fragments));
    }

    /**
     * 创建场景知识绑定。
     *
     * @param request 创建场景绑定请求
     * @return 新建绑定记录
     */
    @Operation(summary = "创建场景知识绑定", description = "将知识片段绑定到 scenarioCode。")
    @PostMapping("/bindings")
    public ApiResponse<ScenarioKnowledgeBinding> createBinding(@RequestBody CreateScenarioBindingRequest request) {
        log.info("收到创建场景知识绑定请求, scenarioCode={}, knowledgeCode={}",
                request.scenarioCode(), request.knowledgeCode());
        return RuntimeResponses.success(knowledgeBindingService.bindScenario(
                request.scenarioCode(),
                request.knowledgeCode(),
                request.priority(),
                request.enabled()));
    }

    /**
     * 查询场景知识绑定列表。
     *
     * @param scenarioCode 场景编码
     * @return 绑定列表
     */
    @Operation(summary = "查询场景绑定列表", description = "查询知识场景绑定关系。")
    @GetMapping("/bindings")
    public ApiResponse<RuntimeListResult<ScenarioKnowledgeBinding>> listBindings(
            @RequestParam(name = "scenarioCode") String scenarioCode) {
        log.info("收到场景知识绑定查询请求, scenarioCode={}", scenarioCode);
        var bindings = knowledgeBindingService.listBindings(scenarioCode);
        return RuntimeResponses.success(new RuntimeListResult<>(bindings.size(), bindings));
    }

    /**
     * 检索知识片段。
     *
     * @param scenarioCode 场景编码
     * @param query        检索关键词
     * @param limit        返回上限
     * @return 检索结果
     */
    @Operation(summary = "检索知识片段", description = "按 scenario/query 检索相关知识，供 Prompt 注入。")
    @GetMapping("/retrieve")
    public ApiResponse<KnowledgeRetrievalResult> retrieve(
            @RequestParam(name = "scenarioCode") String scenarioCode,
            @RequestParam(name = "query", required = false) String query,
            @RequestParam(name = "limit", required = false) Integer limit) {
        log.info("收到知识检索请求, scenarioCode={}, query={}, limit={}", scenarioCode, query, limit);
        var items = knowledgeRetrievalService.retrieve(scenarioCode, query, limit);
        return RuntimeResponses.success(new KnowledgeRetrievalResult(query, scenarioCode, items.size(), items));
    }
}
