package cn.cyc.ai.cog.platform.org.repository;

import cn.cyc.ai.cog.common.exception.Errors;
import cn.cyc.ai.cog.common.exception.PlatformErrorCode;
import cn.cyc.ai.cog.common.exception.ServiceException;
import cn.cyc.ai.cog.platform.org.domain.OrgMember;
import cn.cyc.ai.cog.platform.org.dto.OrgMemberSaveRequest;
import cn.cyc.ai.cog.platform.org.entity.OrgMemberEntity;
import cn.cyc.ai.cog.platform.org.mapper.OrgMemberMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 组织成员 MyBatis 仓储实现。
 */
/**
 * 组织成员仓储 MyBatis 实现。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Repository
public class DbOrgMemberRepository implements OrgMemberRepository {

    /** 组织成员 Mapper */
    private final OrgMemberMapper orgMemberMapper;

    /**
     * @param orgMemberMapper 组织成员 Mapper
     */
    public DbOrgMemberRepository(OrgMemberMapper orgMemberMapper) {
        this.orgMemberMapper = orgMemberMapper;
    }

    /**
     * 执行insertOwner。
     *
     * @param tenantId 租户 ID
     * @param orgId orgID
     * @param userId 用户 ID
     */
    @Override
    public void insertOwner(Long tenantId, Long orgId, Long userId) {
        OrgMemberEntity ownerMember = new OrgMemberEntity();
        ownerMember.setTenantId(tenantId);
        ownerMember.setOrgId(orgId);
        ownerMember.setUserId(userId);
        ownerMember.setOrgRole("OWNER");
        ownerMember.setStatus("ACTIVE");
        orgMemberMapper.insert(ownerMember);
    }

    /**
     * 查询人OrgID列表。
     *
     * @param orgId orgID
     * @return 结果列表
     */
    @Override
    public List<OrgMember> listByOrgId(Long orgId) {
        return orgMemberMapper.selectList(new LambdaQueryWrapper<OrgMemberEntity>()
                        .eq(OrgMemberEntity::getOrgId, orgId)
                        .orderByDesc(OrgMemberEntity::getId))
                .stream()
                .map(this::toDomain)
                .toList();
    }

    /**
     * 执行数量ActiveMembers。
     *
     * @param orgId orgID
     * @return 执行结果
     */
    @Override
    public long countActiveMembers(Long orgId) {
        return orgMemberMapper.countActiveMembers(orgId);
    }

    /**
     * 执行exists人OrgAnd用户。
     *
     * @param orgId orgID
     * @param userId 用户 ID
     * @return 执行结果
     */
    @Override
    public boolean existsByOrgAndUser(Long orgId, Long userId) {
        return orgMemberMapper.selectOne(new LambdaQueryWrapper<OrgMemberEntity>()
                .eq(OrgMemberEntity::getOrgId, orgId)
                .eq(OrgMemberEntity::getUserId, userId)
                .last("LIMIT 1")) != null;
    }

    /**
     * 执行insertMember。
     *
     * @param tenantId 租户 ID
     * @param orgId orgID
     * @param request 请求
     * @return 执行结果
     */
    @Override
    public OrgMember insertMember(Long tenantId, Long orgId, OrgMemberSaveRequest request) {
        OrgMemberEntity member = new OrgMemberEntity();
        member.setTenantId(tenantId);
        member.setOrgId(orgId);
        member.setUserId(request.getUserId());
        member.setDeptId(request.getDeptId());
        member.setOrgRole(StringUtils.hasText(request.getOrgRole()) ? request.getOrgRole() : "MEMBER");
        member.setStatus("ACTIVE");
        orgMemberMapper.insert(member);
        return toDomain(member);
    }

    /**
     * 删除Member。
     *
     * @param orgId orgID
     * @param memberId memberID
     */
    @Override
    public void removeMember(Long orgId, Long memberId) {
        OrgMemberEntity member = orgMemberMapper.selectById(memberId);
        if (member == null || !orgId.equals(member.getOrgId())) {
            throw Errors.of(PlatformErrorCode.ORG_MEMBER_NOT_FOUND);
        }
        if ("OWNER".equals(member.getOrgRole())) {
            throw Errors.of(PlatformErrorCode.ORG_OWNER_NOT_REMOVABLE);
        }
        orgMemberMapper.deleteById(memberId);
    }

    /**
     * 转换为Domain。
     *
     * @param entity 实体
     * @return 转换结果
     */
    private OrgMember toDomain(OrgMemberEntity entity) {
        return new OrgMember(
                entity.getId(),
                entity.getTenantId(),
                entity.getOrgId(),
                entity.getUserId(),
                entity.getDeptId(),
                entity.getOrgRole(),
                entity.getStatus(),
                entity.getJoinedAt(),
                entity.getCreateTime(),
                entity.getUpdateTime()
        );
    }
}
