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

    @Override
    public List<OrgMember> listByOrgId(Long orgId) {
        return orgMemberMapper.selectList(new LambdaQueryWrapper<OrgMemberEntity>()
                        .eq(OrgMemberEntity::getOrgId, orgId)
                        .orderByDesc(OrgMemberEntity::getId))
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public long countActiveMembers(Long orgId) {
        return orgMemberMapper.countActiveMembers(orgId);
    }

    @Override
    public boolean existsByOrgAndUser(Long orgId, Long userId) {
        return orgMemberMapper.selectOne(new LambdaQueryWrapper<OrgMemberEntity>()
                .eq(OrgMemberEntity::getOrgId, orgId)
                .eq(OrgMemberEntity::getUserId, userId)
                .last("LIMIT 1")) != null;
    }

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
