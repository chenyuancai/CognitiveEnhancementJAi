package cn.cyc.ai.cog.admin.ai.dto;

import cn.cyc.ai.cog.runtime.api.ModelGovernanceStateResult;
import cn.cyc.ai.cog.runtime.api.ModelStatusOverviewResult;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * AI 路由/治理只读总览（PRD AI 控制台）。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class AiRoutingOverviewResult {

    /** 模型Overview。 */
    private ModelStatusOverviewResult modelOverview;
    /** governanceStates。 */
    private List<ModelGovernanceStateResult> governanceStates = new ArrayList<>();
    /** 能力Routes。 */
    private List<CapabilityRoutingItem> capabilityRoutes = new ArrayList<>();
}
