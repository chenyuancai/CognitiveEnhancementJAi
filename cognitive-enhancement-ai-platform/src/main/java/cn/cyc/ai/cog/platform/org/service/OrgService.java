package cn.cyc.ai.cog.platform.org.service;

import cn.cyc.ai.cog.common.exception.Errors;
import cn.cyc.ai.cog.common.exception.PlatformErrorCode;
import cn.cyc.ai.cog.platform.org.domain.OrgDepartment;
import cn.cyc.ai.cog.platform.org.domain.OrgMember;
import cn.cyc.ai.cog.platform.org.domain.Organization;
import cn.cyc.ai.cog.platform.org.dto.DepartmentSaveRequest;
import cn.cyc.ai.cog.platform.org.dto.OrgMemberSaveRequest;
import cn.cyc.ai.cog.platform.org.dto.OrgPageQuery;
import cn.cyc.ai.cog.platform.org.repository.OrgDepartmentRepository;
import cn.cyc.ai.cog.platform.org.repository.OrgMemberRepository;
import cn.cyc.ai.cog.platform.org.repository.OrganizationRepository;
import cn.cyc.ai.cog.common.exception.ServiceException;
import cn.cyc.ai.cog.common.page.PageResult;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 组织治理服务：组织、部门与成员管理。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Service
public class OrgService {

    /** 组织仓储 */
    private final OrganizationRepository organizationRepository;

    /** 组织部门仓储 */
    private final OrgDepartmentRepository orgDepartmentRepository;

    /** 组织成员仓储 */
    private final OrgMemberRepository orgMemberRepository;

    /**
     * @param organizationRepository  组织仓储
     * @param orgDepartmentRepository 组织部门仓储
     * @param orgMemberRepository     组织成员仓储
     */
    public OrgService(OrganizationRepository organizationRepository,
                      OrgDepartmentRepository orgDepartmentRepository,
                      OrgMemberRepository orgMemberRepository) {
        this.organizationRepository = organizationRepository;
        this.orgDepartmentRepository = orgDepartmentRepository;
        this.orgMemberRepository = orgMemberRepository;
    }

    /**
     * 分页查询组织。
     *
     * @param query 分页与筛选条件
     * @return 组织分页结果
     */
    public PageResult<Organization> page(OrgPageQuery query) {
        return organizationRepository.page(query);
    }

    /**
     * 查询组织详情。
     *
     * @param orgId 组织 ID
     * @return 组织领域对象
     */
    public Organization detail(Long orgId) {
        return organizationRepository.requireById(orgId);
    }

    /**
     * 查询组织下的部门列表。
     *
     * @param orgId 组织 ID
     * @return 部门列表
     */
    public List<OrgDepartment> listDepartments(Long orgId) {
        organizationRepository.requireById(orgId);
        return orgDepartmentRepository.listByOrgId(orgId);
    }

    /**
     * 创建部门。
     *
     * @param orgId   组织 ID
     * @param request 部门创建请求
     * @return 持久化后的部门
     */
    public OrgDepartment createDepartment(Long orgId, DepartmentSaveRequest request) {
        Organization org = organizationRepository.requireById(orgId);
        return orgDepartmentRepository.insert(org.tenantId(), orgId, request);
    }

    /**
     * 更新部门信息。
     *
     * @param orgId   组织 ID
     * @param deptId  部门 ID
     * @param request 部门更新请求
     * @return 更新后的部门
     */
    public OrgDepartment updateDepartment(Long orgId, Long deptId, DepartmentSaveRequest request) {
        return orgDepartmentRepository.update(orgId, deptId, request);
    }

    /**
     * 删除部门。
     *
     * @param orgId  组织 ID
     * @param deptId 部门 ID
     */
    public void deleteDepartment(Long orgId, Long deptId) {
        orgDepartmentRepository.delete(orgId, deptId);
    }

    /**
     * 查询组织成员列表。
     *
     * @param orgId 组织 ID
     * @return 成员列表
     */
    public List<OrgMember> listMembers(Long orgId) {
        organizationRepository.requireById(orgId);
        return orgMemberRepository.listByOrgId(orgId);
    }

    /**
     * 添加组织成员（校验席位与重复加入）。
     *
     * @param orgId   组织 ID
     * @param request 成员添加请求
     * @return 持久化后的成员
     */
    public OrgMember addMember(Long orgId, OrgMemberSaveRequest request) {
        Organization org = organizationRepository.requireById(orgId);
        long activeCount = orgMemberRepository.countActiveMembers(orgId);
        if (org.seatLimit() != null && activeCount >= org.seatLimit()) {
            throw Errors.of(PlatformErrorCode.ORG_SEAT_FULL);
        }
        if (orgMemberRepository.existsByOrgAndUser(orgId, request.getUserId())) {
            throw Errors.of(PlatformErrorCode.ORG_MEMBER_EXISTS);
        }
        return orgMemberRepository.insertMember(org.tenantId(), orgId, request);
    }

    /**
     * 移除组织成员。
     *
     * @param orgId    组织 ID
     * @param memberId 成员 ID
     */
    public void removeMember(Long orgId, Long memberId) {
        orgMemberRepository.removeMember(orgId, memberId);
    }
}
