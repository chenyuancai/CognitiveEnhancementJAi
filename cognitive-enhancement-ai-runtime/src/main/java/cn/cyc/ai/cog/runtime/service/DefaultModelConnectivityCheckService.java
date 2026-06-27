package cn.cyc.ai.cog.runtime.service;

import cn.cyc.ai.cog.core.exception.BusinessException;
import cn.cyc.ai.cog.core.metadata.model.ModelDefinition;
import cn.cyc.ai.cog.core.metadata.model.ModelDefinitionRepository;
import cn.cyc.ai.cog.core.metadata.type.CommonStatus;
import cn.cyc.ai.cog.core.trace.TraceContext;
import cn.cyc.ai.cog.runtime.api.LlmInvocationRequest;
import cn.cyc.ai.cog.runtime.api.LlmInvocationResult;
import cn.cyc.ai.cog.runtime.api.ModelConnectivityCheckRequest;
import cn.cyc.ai.cog.runtime.api.ModelConnectivityCheckResult;
import cn.cyc.ai.cog.runtime.domain.ModelCheckRecord;
import cn.cyc.ai.cog.runtime.spi.ModelConnectivityCheckService;
import cn.cyc.ai.cog.runtime.spi.LlmProviderHandler;
import cn.cyc.ai.cog.runtime.spi.ModelCheckRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * 默认模型连通性检查服务。
 *
 * @author cyc
 */
@Service
public class DefaultModelConnectivityCheckService implements ModelConnectivityCheckService {

    /**
     * 检查日志。
     */
    private static final Logger log = LoggerFactory.getLogger(DefaultModelConnectivityCheckService.class);

    /**
     * 默认检查提示词。
     */
    private static final String DEFAULT_PROMPT = "请回复：模型连通性检查成功。";

    /**
     * 模型定义仓储。
     */
    private final ModelDefinitionRepository modelDefinitionRepository;

    /**
     * LLM Provider 处理器列表。
     */
    private final List<LlmProviderHandler> llmProviderHandlers;

    /**
     * 模型检查记录仓储。
     */
    private final ModelCheckRecordRepository modelCheckRecordRepository;

    /**
     * 构造默认模型检查服务。
     *
     * @param modelDefinitionRepository 模型定义仓储
     * @param llmProviderHandlers       Provider 处理器列表
     */
    public DefaultModelConnectivityCheckService(ModelDefinitionRepository modelDefinitionRepository,
                                                List<LlmProviderHandler> llmProviderHandlers,
                                                ModelCheckRecordRepository modelCheckRecordRepository) {
        this.modelDefinitionRepository = modelDefinitionRepository;
        this.llmProviderHandlers = llmProviderHandlers;
        this.modelCheckRecordRepository = modelCheckRecordRepository;
    }

    /**
     * 执行一次模型连通性检查。
     *
     * @param request 检查请求
     * @return 检查结果
     */
    @Override
    public ModelConnectivityCheckResult check(ModelConnectivityCheckRequest request) {
        if (request == null || !StringUtils.hasText(request.modelCode())) {
            throw new BusinessException("INVALID_ARGUMENT", "modelCode 不能为空");
        }
        ModelDefinition model = modelDefinitionRepository.findByCode(request.modelCode())
                .orElseThrow(() -> new BusinessException("NOT_FOUND", "未找到模型定义: " + request.modelCode()));
        if (model.status() != CommonStatus.ENABLED) {
            log.warn("模型连通性检查失败，模型未启用, modelCode={}", model.modelCode());
            return saveAndReturn(failure(model, 0L, "模型未启用: " + model.modelCode()));
        }
        LlmProviderHandler handler = llmProviderHandlers.stream()
                .filter(candidate -> candidate.supports(model.providerCode()))
                .findFirst()
                .orElse(null);
        if (handler == null) {
            log.warn("模型连通性检查失败，未找到 Provider 处理器, modelCode={}, providerCode={}",
                    model.modelCode(), model.providerCode());
            return saveAndReturn(failure(model, 0L, "未找到可用的 LLM Provider 处理器: " + model.providerCode()));
        }
        String prompt = StringUtils.hasText(request.prompt()) ? request.prompt() : DEFAULT_PROMPT;
        long startTime = System.currentTimeMillis();
        try {
            LlmInvocationResult result = handler.generate(new LlmInvocationRequest(
                    TraceContext.getTraceId(),
                    "runtime.model.check",
                    "runtime.model.check",
                    model.providerCode(),
                    model.modelCode(),
                    model.endpoint(),
                    model.apiKey(),
                    model.timeoutMs(),
                    null,
                    prompt,
                    request.parameters()
            ));
            long latencyMs = System.currentTimeMillis() - startTime;
            log.info("模型连通性检查成功, modelCode={}, providerCode={}, latencyMs={}, mock={}",
                    model.modelCode(), model.providerCode(), latencyMs, result.mock());
            return saveAndReturn(new ModelConnectivityCheckResult(
                    true,
                    model.providerCode(),
                    model.modelCode(),
                    latencyMs,
                    result.mock(),
                    null,
                    preview(result.answer())
            ));
        } catch (RuntimeException exception) {
            long latencyMs = System.currentTimeMillis() - startTime;
            String failureReason = StringUtils.hasText(exception.getMessage()) ? exception.getMessage() : exception.getClass().getSimpleName();
            log.warn("模型连通性检查失败, modelCode={}, providerCode={}, latencyMs={}, reason={}",
                    model.modelCode(), model.providerCode(), latencyMs, failureReason);
            return saveAndReturn(failure(model, latencyMs, failureReason));
        }
    }

    /**
     * 保存模型检查记录并返回结果。
     *
     * @param result 检查结果
     * @return 原始检查结果
     */
    private ModelConnectivityCheckResult saveAndReturn(ModelConnectivityCheckResult result) {
        modelCheckRecordRepository.save(new ModelCheckRecord(
                TraceContext.getTraceId(),
                result.providerCode(),
                result.modelCode(),
                result.reachable(),
                result.latencyMs(),
                result.mock(),
                result.failureReason(),
                result.answerPreview(),
                Instant.now()
        ));
        return result;
    }

    /**
     * 构造失败结果。
     *
     * @param model         模型定义
     * @param latencyMs     检查耗时
     * @param failureReason 失败原因
     * @return 失败结果
     */
    private ModelConnectivityCheckResult failure(ModelDefinition model, long latencyMs, String failureReason) {
        return new ModelConnectivityCheckResult(
                false,
                model.providerCode(),
                model.modelCode(),
                latencyMs,
                false,
                failureReason,
                null
        );
    }

    /**
     * 构造回答预览。
     *
     * @param answer 原始回答
     * @return 预览内容
     */
    private String preview(String answer) {
        if (!StringUtils.hasText(answer)) {
            return null;
        }
        if (answer.length() <= 120) {
            return answer;
        }
        return answer.substring(0, 120);
    }
}
