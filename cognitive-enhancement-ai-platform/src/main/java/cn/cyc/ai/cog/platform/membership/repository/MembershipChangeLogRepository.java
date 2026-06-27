package cn.cyc.ai.cog.platform.membership.repository;

import cn.cyc.ai.cog.common.page.PageResult;
import cn.cyc.ai.cog.platform.membership.domain.MembershipChangeLog;

public interface MembershipChangeLogRepository {

    PageResult<MembershipChangeLog> page(long current, long size, Long accountId);

    void insert(Long accountId, String fromLevel, String toLevel, String changeType, String remark);
}
