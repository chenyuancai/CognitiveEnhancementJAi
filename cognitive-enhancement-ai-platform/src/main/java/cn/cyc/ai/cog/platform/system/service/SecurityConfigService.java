package cn.cyc.ai.cog.platform.system.service;

import cn.cyc.ai.cog.common.page.PageResult;
import cn.cyc.ai.cog.platform.system.domain.SecurityConfig;
import cn.cyc.ai.cog.platform.system.dto.SecurityConfigPageQuery;
import cn.cyc.ai.cog.platform.system.dto.SecurityConfigSaveRequest;
import cn.cyc.ai.cog.platform.system.repository.SecurityConfigRepository;
import cn.cyc.ai.cog.platform.system.support.PlatformLocalCache;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 安全配置查询与维护服务。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Service
public class SecurityConfigService {

    /** 安全配置仓储 */
    private final SecurityConfigRepository securityConfigRepository;

    /** 本地配置缓存 */
    private final PlatformLocalCache platformLocalCache;

    /**
     * @param securityConfigRepository 安全配置仓储
     * @param platformLocalCache       本地配置缓存
     */
    public SecurityConfigService(SecurityConfigRepository securityConfigRepository,
                                 PlatformLocalCache platformLocalCache) {
        this.securityConfigRepository = securityConfigRepository;
        this.platformLocalCache = platformLocalCache;
    }

    /**
     * 分页查询安全配置。
     *
     * @param query 分页与筛选条件
     * @return 安全配置分页结果
     */
    public PageResult<SecurityConfig> page(SecurityConfigPageQuery query) {
        return securityConfigRepository.page(query);
    }

    /**
     * 保存安全配置（新建或更新）。
     *
     * @param id      配置 ID，新建时为 null
     * @param request 保存请求
     * @return 持久化后的配置
     */
    public SecurityConfig save(Long id, SecurityConfigSaveRequest request) {
        SecurityConfig saved = securityConfigRepository.save(id, request);
        platformLocalCache.invalidatePrefix("sec:");
        return saved;
    }

    /**
     * 读取布尔型配置，缺省或非法时返回默认值。
     *
     * @param configKey    配置键
     * @param defaultValue 默认值
     * @return 配置布尔值
     */
    public boolean getBoolean(String configKey, boolean defaultValue) {
        return platformLocalCache.get("sec:bool:" + configKey, Boolean.class, () -> {
            SecurityConfig config = securityConfigRepository.findByConfigKey(configKey);
            if (config == null || !StringUtils.hasText(config.configValue())) {
                return defaultValue;
            }
            String value = config.configValue().trim();
            return "true".equalsIgnoreCase(value) || "1".equals(value) || "yes".equalsIgnoreCase(value);
        });
    }

    /**
     * 读取整型配置，缺省或非法时返回默认值。
     *
     * @param configKey    配置键
     * @param defaultValue 默认值
     * @return 配置整数值
     */
    public int getInt(String configKey, int defaultValue) {
        return platformLocalCache.get("sec:int:" + configKey, Integer.class, () -> {
            SecurityConfig config = securityConfigRepository.findByConfigKey(configKey);
            if (config == null || !StringUtils.hasText(config.configValue())) {
                return defaultValue;
            }
            try {
                return Integer.parseInt(config.configValue().trim());
            } catch (NumberFormatException ex) {
                return defaultValue;
            }
        });
    }
}
