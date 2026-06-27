package cn.cyc.ai.cog.platform.iam.repository;

import cn.cyc.ai.cog.common.page.PageResult;
import cn.cyc.ai.cog.platform.iam.domain.Tenant;
import cn.cyc.ai.cog.platform.iam.dto.TenantPageQuery;
import cn.cyc.ai.cog.platform.iam.dto.TenantSaveRequest;

/**
 * 租户仓储接口。
 */
public interface TenantRepository {

    /**
     * 分页查询租户。
     *
     * @param query 分页与筛选条件
     * @return 租户分页结果
     */
    PageResult<Tenant> page(TenantPageQuery query);

    /**
     * 按 ID 查询租户，不存在时抛出业务异常。
     *
     * @param id 租户 ID
     * @return 租户领域对象
     */
    Tenant requireById(Long id);

    /**
     * 创建租户。
     *
     * @param request 创建请求
     * @return 持久化后的租户
     */
    Tenant create(TenantSaveRequest request);

    /**
     * 更新租户。
     *
     * @param id      租户 ID
     * @param request 更新请求
     * @return 更新后的租户
     */
    Tenant update(Long id, TenantSaveRequest request);

    /**
     * 更新租户状态。
     *
     * @param id     租户 ID
     * @param status 目标状态
     * @return 更新后的租户
     */
    Tenant updateStatus(Long id, String status);

    /**
     * 组织开户场景：插入独立租户。
     *
     * @param tenantCode 租户编码
     * @param tenantName 租户名称
     * @param segment    业务分段
     * @return 持久化后的租户
     */
    Tenant insertForOrganization(String tenantCode, String tenantName, String segment);
}
