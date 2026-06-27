package cn.cyc.ai.cog.platform.system.service;

import cn.cyc.ai.cog.common.page.PageResult;
import cn.cyc.ai.cog.platform.system.domain.FeatureSwitch;
import cn.cyc.ai.cog.platform.system.dto.FeatureSwitchPageQuery;
import cn.cyc.ai.cog.platform.system.dto.FeatureSwitchSaveRequest;
import cn.cyc.ai.cog.platform.system.repository.FeatureSwitchRepository;
import cn.cyc.ai.cog.platform.system.support.PlatformLocalCache;
import org.springframework.stereotype.Service;

@Service
public class FeatureSwitchService {

    private final FeatureSwitchRepository featureSwitchRepository;

    private final PlatformLocalCache platformLocalCache;

    public FeatureSwitchService(FeatureSwitchRepository featureSwitchRepository,
                                PlatformLocalCache platformLocalCache) {
        this.featureSwitchRepository = featureSwitchRepository;
        this.platformLocalCache = platformLocalCache;
    }

    public PageResult<FeatureSwitch> page(FeatureSwitchPageQuery query) {
        return featureSwitchRepository.page(query);
    }

    public FeatureSwitch save(Long id, FeatureSwitchSaveRequest request) {
        FeatureSwitch saved = featureSwitchRepository.save(id, request);
        platformLocalCache.invalidatePrefix("feature:");
        return saved;
    }
}
