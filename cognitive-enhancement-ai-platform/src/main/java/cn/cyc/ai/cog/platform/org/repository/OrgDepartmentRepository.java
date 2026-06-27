package cn.cyc.ai.cog.platform.org.repository;

import cn.cyc.ai.cog.platform.org.domain.OrgDepartment;
import cn.cyc.ai.cog.platform.org.dto.DepartmentSaveRequest;

import java.util.List;

/**
 * 组织部门仓储接口。
 */
public interface OrgDepartmentRepository {

    /**
     * 查询组织下全部部门。
     *
     * @param orgId 组织 ID
     * @return 部门列表
     */
    List<OrgDepartment> listByOrgId(Long orgId);

    /**
     * 新增部门。
     *
     * @param tenantId 租户 ID
     * @param orgId    组织 ID
     * @param request  保存请求
     * @return 持久化后的部门
     */
    OrgDepartment insert(Long tenantId, Long orgId, DepartmentSaveRequest request);

    /**
     * 更新部门。
     *
     * @param orgId   组织 ID
     * @param deptId  部门 ID
     * @param request 保存请求
     * @return 更新后的部门
     */
    OrgDepartment update(Long orgId, Long deptId, DepartmentSaveRequest request);

    /**
     * 删除部门。
     *
     * @param orgId  组织 ID
     * @param deptId 部门 ID
     */
    void delete(Long orgId, Long deptId);
}
