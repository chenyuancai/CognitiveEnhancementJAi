package cn.cyc.ai.cog.admin.ai.dto;

import cn.cyc.ai.cog.runtime.api.ModelGovernanceStateResult;
import cn.cyc.ai.cog.runtime.api.ModelStatusOverviewResult;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * AI 路由/治理只读总览（PRD AI 控制台）。
 */
@Data
public class AiRoutingOverviewResult {

    private ModelStatusOverviewResult modelOverview;
    private List<ModelGovernanceStateResult> governanceStates = new ArrayList<>();
    private List<CapabilityRoutingItem> capabilityRoutes = new ArrayList<>();
}
