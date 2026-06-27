package cn.cyc.ai.cog.platform.org.repository;

import cn.cyc.ai.cog.platform.org.domain.OrgMember;
import cn.cyc.ai.cog.platform.org.dto.OrgMemberSaveRequest;

import java.util.List;

/**
 * 组织成员仓储接口。
 */
public interface OrgMemberRepository {

    /**
     * 插入组织所有者成员记录。
     *
     * @param tenantId 租户 ID
     * @param orgId    组织 ID
     * @param userId   用户 ID
     */
    void insertOwner(Long tenantId, Long orgId, Long userId);

    /**
     * 查询组织成员列表。
     *
     * @param orgId 组织 ID
     * @return 成员列表
     */
    List<OrgMember> listByOrgId(Long orgId);

    /**
     * 统计组织活跃成员数。
     *
     * @param orgId 组织 ID
     * @return 活跃成员数
     */
    long countActiveMembers(Long orgId);

    /**
     * 判断用户是否已在组织中。
     *
     * @param orgId  组织 ID
     * @param userId 用户 ID
     * @return 是否已存在
     */
    boolean existsByOrgAndUser(Long orgId, Long userId);

    /**
     * 添加组织成员。
     *
     * @param tenantId 租户 ID
     * @param orgId    组织 ID
     * @param request  成员保存请求
     * @return 持久化后的成员
     */
    OrgMember insertMember(Long tenantId, Long orgId, OrgMemberSaveRequest request);

    /**
     * 移除组织成员。
     *
     * @param orgId    组织 ID
     * @param memberId 成员 ID
     */
    void removeMember(Long orgId, Long memberId);
}
