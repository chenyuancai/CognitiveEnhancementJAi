package cn.cyc.ai.cog.platform.membership.repository;

import cn.cyc.ai.cog.common.page.PageResult;
import cn.cyc.ai.cog.platform.membership.domain.MembershipLevel;
import cn.cyc.ai.cog.platform.membership.dto.MembershipLevelPageQuery;
import cn.cyc.ai.cog.platform.membership.dto.MembershipLevelSaveRequest;

import java.util.List;

public interface MembershipLevelRepository {

    PageResult<MembershipLevel> page(MembershipLevelPageQuery query);

    List<MembershipLevel> listEnabled(String segment);

    MembershipLevel findById(Long id);

    MembershipLevel findByCode(String levelCode);

    /**
     * 按编码查询会员等级，不存在时返回 null。
     *
     * @param levelCode 等级编码
     * @return 会员等级或 null
     */
    MembershipLevel findByCodeIfPresent(String levelCode);

    /**
     * 查询指定分段的默认会员等级，不存在时回退 FREE 等级。
     *
     * @param segment 业务分段
     * @return 默认会员等级
     */
    MembershipLevel requireDefaultForSegment(String segment);

    MembershipLevel create(MembershipLevelSaveRequest request);

    MembershipLevel update(Long id, MembershipLevelSaveRequest request);
}
