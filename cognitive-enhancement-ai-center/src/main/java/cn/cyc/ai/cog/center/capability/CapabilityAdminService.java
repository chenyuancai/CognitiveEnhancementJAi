package cn.cyc.ai.cog.center.capability;

import cn.cyc.ai.cog.center.common.CenterPageResult;
import cn.cyc.ai.cog.center.support.AbstractMetadataAdminService;
import cn.cyc.ai.cog.core.metadata.capability.CapabilityDefinition;
import cn.cyc.ai.cog.core.metadata.capability.CapabilityDefinitionRepository;
import cn.cyc.ai.cog.core.metadata.type.ExecutionMode;
import cn.cyc.ai.cog.core.metadata.type.RiskLevel;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 能力管理服务。
 *
 * @author cyc
 */
@Service
public class CapabilityAdminService extends AbstractMetadataAdminService<CapabilityDefinition, CapabilityUpsertRequest, CapabilityResult> {

    /**
     * 创建能力后台管理服务。
     *
     * @param repository 能力定义仓储
     */
    public CapabilityAdminService(CapabilityDefinitionRepository repository) {
        super(repository);
    }

    /**
     * 分页查询能力定义。
     *
     * @param query 查询参数
     * @return 分页能力列表
     */
    public CenterPageResult<CapabilityResult> listPage(CapabilityPageQuery query) {
        return listPage(
                query,
                definition -> matches(query.getBoundAgentCode(), definition.boundAgentCode())
                        && matches(query.getRiskLevel(), definition.riskLevel())
                        && matches(query.getExecuteMode(), definition.executeMode()),
                CapabilityDefinition::status,
                capabilitySorters()
        );
    }

    /**
     * 将能力写入请求转换为能力定义。
     *
     * @param request      能力写入请求
     * @param overrideCode 覆盖编码
     * @return 能力定义
     */
    @Override
    protected CapabilityDefinition toDefinition(CapabilityUpsertRequest request, String overrideCode) {
        String capabilityCode = overrideCode != null ? overrideCode : Objects.requireNonNull(request.capabilityCode(), "capabilityCode 不能为空");
        if (overrideCode != null) {
            CapabilityDefinition existing = findDefinition(capabilityCode);
            return new CapabilityDefinition(
                    capabilityCode,
                    request.capabilityName(),
                    request.capabilityDesc(),
                    request.inputSchema(),
                    request.outputSchema(),
                    request.parameterConstraints(),
                    request.executeMode(),
                    request.boundAgentCode(),
                    request.riskLevel(),
                    request.needHumanConfirm(),
                    request.status(),
                    existing.version(),
                    existing.publishedAt(),
                    existing.lifecycleStatus()
            );
        }
        return new CapabilityDefinition(
                capabilityCode,
                request.capabilityName(),
                request.capabilityDesc(),
                request.inputSchema(),
                request.outputSchema(),
                request.parameterConstraints(),
                request.executeMode(),
                request.boundAgentCode(),
                request.riskLevel(),
                request.needHumanConfirm(),
                request.status()
        );
    }

    /**
     * 将能力定义转换为返回对象。
     *
     * @param definition 能力定义
     * @return 能力返回对象
     */
    @Override
    protected CapabilityResult toResult(CapabilityDefinition definition) {
        return new CapabilityResult(
                definition.capabilityCode(),
                definition.capabilityName(),
                definition.capabilityDesc(),
                definition.inputSchema(),
                definition.outputSchema(),
                definition.parameterConstraints(),
                definition.executeMode(),
                definition.boundAgentCode(),
                definition.riskLevel(),
                definition.needHumanConfirm(),
                definition.status(),
                definition.version(),
                definition.lifecycleStatus(),
                definition.publishedAt()
        );
    }

    private Map<String, Comparator<CapabilityDefinition>> capabilitySorters() {
        Map<String, Comparator<CapabilityDefinition>> sorters = new LinkedHashMap<>(commonSorters(CapabilityDefinition::status));
        sorters.put("boundAgentCode", Comparator.comparing(CapabilityDefinition::boundAgentCode));
        sorters.put("riskLevel", Comparator.comparing(definition -> definition.riskLevel().name()));
        sorters.put("executeMode", Comparator.comparing(definition -> definition.executeMode().name()));
        return sorters;
    }

    private boolean matches(String expected, String actual) {
        return !StringUtils.hasText(expected) || expected.equals(actual);
    }

    private boolean matches(RiskLevel expected, RiskLevel actual) {
        return expected == null || expected == actual;
    }

    private boolean matches(ExecutionMode expected, ExecutionMode actual) {
        return expected == null || expected == actual;
    }
}
