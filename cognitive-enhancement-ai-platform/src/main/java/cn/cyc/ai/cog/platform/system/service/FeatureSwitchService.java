package cn.cyc.ai.cog.platform.system.service;

import cn.cyc.ai.cog.common.page.PageResult;
import cn.cyc.ai.cog.platform.system.domain.FeatureSwitch;
import cn.cyc.ai.cog.platform.system.dto.FeatureSwitchPageQuery;
import cn.cyc.ai.cog.platform.system.dto.FeatureSwitchSaveRequest;
import cn.cyc.ai.cog.platform.system.repository.FeatureSwitchRepository;
import cn.cyc.ai.cog.platform.system.support.PlatformLocalCache;
import org.springframework.stereotype.Service;

/**
 * FeatureSwitch服务
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Service
public class FeatureSwitchService {

    /** featureSwitch仓储。 */
    private final FeatureSwitchRepository featureSwitchRepository;

    /** 平台Local缓存。 */
    private final PlatformLocalCache platformLocalCache;

    /**
     * 创建FeatureSwitch服务。
     */
    public FeatureSwitchService(FeatureSwitchRepository featureSwitchRepository,
                                PlatformLocalCache platformLocalCache) {
        this.featureSwitchRepository = featureSwitchRepository;
        this.platformLocalCache = platformLocalCache;
    }

    /**
     * 执行分页。
     *
     * @param query 查询
     * @return 执行结果
     */
    public PageResult<FeatureSwitch> page(FeatureSwitchPageQuery query) {
        return featureSwitchRepository.page(query);
    }

    /**
     * 执行save。
     *
     * @param id 主键 ID
     * @param request 请求
     * @return 执行结果
     */
    public FeatureSwitch save(Long id, FeatureSwitchSaveRequest request) {
        FeatureSwitch saved = featureSwitchRepository.save(id, request);
        platformLocalCache.invalidatePrefix("feature:");
        return saved;
    }
}
