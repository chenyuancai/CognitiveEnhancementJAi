package cn.cyc.ai.cog.runtime.service;

import cn.cyc.ai.cog.core.exception.BusinessException;
import cn.cyc.ai.cog.core.metadata.model.ModelDefinitionRepository;
import cn.cyc.ai.cog.core.metadata.type.CommonStatus;
import cn.cyc.ai.cog.runtime.api.ModelConnectivityCheckRequest;
import cn.cyc.ai.cog.runtime.api.ModelConnectivityCheckResult;
import cn.cyc.ai.cog.runtime.api.ModelStatusRefreshRequest;
import cn.cyc.ai.cog.runtime.api.RuntimeListResult;
import cn.cyc.ai.cog.runtime.spi.ModelConnectivityCheckService;
import cn.cyc.ai.cog.runtime.spi.ModelStatusRefreshService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * 默认模型状态刷新服务。
 *
 * @author cyc
 */
@Service
public class DefaultModelStatusRefreshService implements ModelStatusRefreshService {

    /**
     * 服务日志。
     */
    private static final Logger log = LoggerFactory.getLogger(DefaultModelStatusRefreshService.class);

    /**
     * 模型连通性检查服务。
     */
    private final ModelConnectivityCheckService modelConnectivityCheckService;

    /**
     * 模型定义仓储。
     */
    private final ModelDefinitionRepository modelDefinitionRepository;

    /**
     * 构造模型状态刷新服务。
     *
     * @param modelConnectivityCheckService 模型连通性检查服务
     * @param modelDefinitionRepository     模型定义仓储
     */
    public DefaultModelStatusRefreshService(ModelConnectivityCheckService modelConnectivityCheckService,
                                            ModelDefinitionRepository modelDefinitionRepository) {
        this.modelConnectivityCheckService = modelConnectivityCheckService;
        this.modelDefinitionRepository = modelDefinitionRepository;
    }

    /**
     * 刷新一个或多个模型状态。
     *
     * @param request 刷新请求
     * @return 刷新结果列表
     */
    @Override
    public RuntimeListResult<ModelConnectivityCheckResult> refresh(ModelStatusRefreshRequest request) {
        Set<String> modelCodes = resolveModelCodes(request);
        log.info("开始刷新模型状态, total={}, modelCodes={}", modelCodes.size(), modelCodes);
        List<ModelConnectivityCheckResult> items = new ArrayList<>(modelCodes.size());
        for (String modelCode : modelCodes) {
            items.add(modelConnectivityCheckService.check(new ModelConnectivityCheckRequest(
                    modelCode,
                    request == null ? null : request.prompt(),
                    request == null ? null : request.parameters()
            )));
        }
        log.info("模型状态刷新完成, total={}", items.size());
        return new RuntimeListResult<>(items.size(), items);
    }

    /**
     * 归并刷新请求中的模型编码集合。
     *
     * @param request 刷新请求
     * @return 去重后的模型编码集合
     */
    private Set<String> resolveModelCodes(ModelStatusRefreshRequest request) {
        if (request == null) {
            throw new BusinessException("INVALID_ARGUMENT", "模型状态刷新请求不能为空");
        }
        Set<String> modelCodes = new LinkedHashSet<>();
        addModelCode(modelCodes, request.modelCode());
        if (request.modelCodes() != null) {
            request.modelCodes().forEach(modelCode -> addModelCode(modelCodes, modelCode));
        }
        if (modelCodes.isEmpty()) {
            modelDefinitionRepository.listAll().stream()
                    .filter(model -> model.status() == CommonStatus.ENABLED)
                    .map(model -> model.modelCode())
                    .forEach(modelCodes::add);
        }
        if (modelCodes.isEmpty()) {
            throw new BusinessException("CONFLICT", "当前没有可刷新的已启用模型");
        }
        return modelCodes;
    }

    /**
     * 向集合追加单个模型编码。
     *
     * @param modelCodes 模型编码集合
     * @param modelCode  单个模型编码
     */
    private void addModelCode(Set<String> modelCodes, String modelCode) {
        if (modelCode != null && !modelCode.isBlank()) {
            modelCodes.add(modelCode);
        }
    }
}
