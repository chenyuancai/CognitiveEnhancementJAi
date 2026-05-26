package cn.cyc.ai.cog.center.capability;

import cn.cyc.ai.cog.center.support.AbstractMetadataAdminService;
import cn.cyc.ai.cog.core.metadata.capability.CapabilityDefinition;
import cn.cyc.ai.cog.core.metadata.capability.CapabilityDefinitionRepository;
import org.springframework.stereotype.Service;

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
     * 将能力写入请求转换为能力定义。
     *
     * @param request      能力写入请求
     * @param overrideCode 覆盖编码
     * @return 能力定义
     */
    @Override
    protected CapabilityDefinition toDefinition(CapabilityUpsertRequest request, String overrideCode) {
        String capabilityCode = overrideCode != null ? overrideCode : Objects.requireNonNull(request.capabilityCode(), "capabilityCode 不能为空");
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
                definition.status()
        );
    }
}
