package cn.cyc.ai.cog.platform.iam.service;

import cn.cyc.ai.cog.platform.iam.domain.Tenant;
import cn.cyc.ai.cog.platform.iam.dto.TenantPageQuery;
import cn.cyc.ai.cog.platform.iam.dto.TenantSaveRequest;
import cn.cyc.ai.cog.platform.iam.repository.TenantRepository;
import cn.cyc.ai.cog.common.page.PageResult;
import org.springframework.stereotype.Service;

/**
 * 平台租户管理服务。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Service
public class TenantService {

    /** 租户仓储 */
    private final TenantRepository tenantRepository;

    /**
     * @param tenantRepository 租户仓储
     */
    public TenantService(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    /**
     * 分页查询租户。
     *
     * @param query 分页与筛选条件
     * @return 租户分页结果
     */
    public PageResult<Tenant> page(TenantPageQuery query) {
        return tenantRepository.page(query);
    }

    /**
     * 查询租户详情。
     *
     * @param id 租户 ID
     * @return 租户领域对象
     */
    public Tenant detail(Long id) {
        return tenantRepository.requireById(id);
    }

    /**
     * 创建租户。
     *
     * @param request 创建请求
     * @return 持久化后的租户
     */
    public Tenant create(TenantSaveRequest request) {
        return tenantRepository.create(request);
    }

    /**
     * 更新租户信息。
     *
     * @param id      租户 ID
     * @param request 更新请求
     * @return 更新后的租户
     */
    public Tenant update(Long id, TenantSaveRequest request) {
        return tenantRepository.update(id, request);
    }

    /**
     * 更新租户状态。
     *
     * @param id     租户 ID
     * @param status 目标状态
     * @return 更新后的租户
     */
    public Tenant updateStatus(Long id, String status) {
        return tenantRepository.updateStatus(id, status);
    }
}
