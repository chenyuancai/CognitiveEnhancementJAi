package cn.cyc.ai.cog.platform.system.repository;

import cn.cyc.ai.cog.common.page.PageResult;
import cn.cyc.ai.cog.platform.system.domain.FeatureSwitch;
import cn.cyc.ai.cog.platform.system.dto.FeatureSwitchPageQuery;
import cn.cyc.ai.cog.platform.system.dto.FeatureSwitchSaveRequest;

public interface FeatureSwitchRepository {

    PageResult<FeatureSwitch> page(FeatureSwitchPageQuery query);

    FeatureSwitch save(Long id, FeatureSwitchSaveRequest request);

    /**
     * 查询全部已启用的功能开关。
     *
     * @return 功能开关列表
     */
    java.util.List<FeatureSwitch> listEnabled();
}
