package cn.cyc.ai.cog.platform.system.repository;

import cn.cyc.ai.cog.common.page.PageResult;
import cn.cyc.ai.cog.platform.system.domain.SecurityConfig;
import cn.cyc.ai.cog.platform.system.dto.SecurityConfigPageQuery;
import cn.cyc.ai.cog.platform.system.dto.SecurityConfigSaveRequest;

/**
 * Security配置仓储
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public interface SecurityConfigRepository {

    PageResult<SecurityConfig> page(SecurityConfigPageQuery query);

    SecurityConfig save(Long id, SecurityConfigSaveRequest request);

    /**
     * 按配置键查询，不存在时返回 null。
     *
     * @param configKey 配置键
     * @return 安全配置或 null
     */
    SecurityConfig findByConfigKey(String configKey);
}
