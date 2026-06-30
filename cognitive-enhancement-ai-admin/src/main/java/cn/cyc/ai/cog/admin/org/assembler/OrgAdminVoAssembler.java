package cn.cyc.ai.cog.admin.org.assembler;

import cn.cyc.ai.cog.admin.org.dto.OrgDepartmentVO;
import cn.cyc.ai.cog.admin.org.dto.OrgMemberVO;
import cn.cyc.ai.cog.admin.org.dto.OrganizationVO;
import cn.cyc.ai.cog.platform.org.domain.OrgDepartment;
import cn.cyc.ai.cog.platform.org.domain.OrgMember;
import cn.cyc.ai.cog.platform.org.domain.Organization;
import org.springframework.stereotype.Component;

/**
 * 管理端组织域领域对象 → VO 转换器。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Component
public class OrgAdminVoAssembler {

    /**
     * 组织领域对象转 VO。
     *
     * @param organization 组织领域对象
     * @return 组织 VO
     */
    public OrganizationVO toOrgVo(Organization organization) {
        OrganizationVO vo = new OrganizationVO();
        vo.setId(organization.id());
        vo.setTenantId(organization.tenantId());
        vo.setAccountId(organization.accountId());
        vo.setOrgType(organization.orgType());
        vo.setOrgName(organization.orgName());
        vo.setUnifiedSocialCode(organization.unifiedSocialCode());
        vo.setSeatLimit(organization.seatLimit());
        vo.setContactName(organization.contactName());
        vo.setContactPhone(organization.contactPhone());
        vo.setCreateTime(organization.createTime());
        vo.setUpdateTime(organization.updateTime());
        return vo;
    }

    /**
     * 部门领域对象转 VO。
     *
     * @param dept 部门领域对象
     * @return 部门 VO
     */
    public OrgDepartmentVO toDeptVo(OrgDepartment dept) {
        OrgDepartmentVO vo = new OrgDepartmentVO();
        vo.setId(dept.id());
        vo.setTenantId(dept.tenantId());
        vo.setOrgId(dept.orgId());
        vo.setParentId(dept.parentId());
        vo.setDeptName(dept.deptName());
        vo.setSortNo(dept.sortNo());
        vo.setCreateTime(dept.createTime());
        vo.setUpdateTime(dept.updateTime());
        return vo;
    }

    /**
     * 组织成员领域对象转 VO。
     *
     * @param member 组织成员领域对象
     * @return 组织成员 VO
     */
    public OrgMemberVO toMemberVo(OrgMember member) {
        OrgMemberVO vo = new OrgMemberVO();
        vo.setId(member.id());
        vo.setTenantId(member.tenantId());
        vo.setOrgId(member.orgId());
        vo.setUserId(member.userId());
        vo.setDeptId(member.deptId());
        vo.setOrgRole(member.orgRole());
        vo.setStatus(member.status());
        vo.setJoinedAt(member.joinedAt());
        vo.setCreateTime(member.createTime());
        vo.setUpdateTime(member.updateTime());
        return vo;
    }
}
