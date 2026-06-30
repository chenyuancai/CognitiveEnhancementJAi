package cn.cyc.ai.cog.platform.org.repository;

import cn.cyc.ai.cog.common.page.PageResult;
import cn.cyc.ai.cog.platform.org.domain.Organization;
import cn.cyc.ai.cog.platform.org.dto.OrgPageQuery;

/**
 * 组织仓储接口。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public interface OrganizationRepository {

    /**
     * 分页查询组织。
     *
     * @param query 分页与筛选条件
     * @return 组织分页结果
     */
    PageResult<Organization> page(OrgPageQuery query);

    /**
     * 按 ID 查询组织，不存在时抛出业务异常。
     *
     * @param orgId 组织 ID
     * @return 组织领域对象
     */
    Organization requireById(Long orgId);

    /**
     * 按商业账户 ID 查询组织，不存在时返回 null。
     *
     * @param accountId 账户 ID
     * @return 组织领域对象或 null
     */
    Organization findByAccountId(Long accountId);

    /**
     * 新增组织并返回持久化后的领域对象。
     *
     * @param organization 待插入组织（id 可为 null）
     * @return 持久化后的组织
     */
    Organization insert(Organization organization);
}
