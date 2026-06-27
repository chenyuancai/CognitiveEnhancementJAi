package cn.cyc.ai.cog.platform.org.repository;

import cn.cyc.ai.cog.common.exception.Errors;
import cn.cyc.ai.cog.common.exception.PlatformErrorCode;
import cn.cyc.ai.cog.common.exception.ServiceException;
import cn.cyc.ai.cog.platform.org.domain.OrgDepartment;
import cn.cyc.ai.cog.platform.org.dto.DepartmentSaveRequest;
import cn.cyc.ai.cog.platform.org.entity.OrgDepartmentEntity;
import cn.cyc.ai.cog.platform.org.mapper.OrgDepartmentMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 组织部门 MyBatis 仓储实现。
 */
/**
 * 组织部门仓储 MyBatis 实现。
 */
@Repository
public class DbOrgDepartmentRepository implements OrgDepartmentRepository {

    /** 组织部门 Mapper */
    private final OrgDepartmentMapper departmentMapper;

    /**
     * @param departmentMapper 组织部门 Mapper
     */
    public DbOrgDepartmentRepository(OrgDepartmentMapper departmentMapper) {
        this.departmentMapper = departmentMapper;
    }

    @Override
    public List<OrgDepartment> listByOrgId(Long orgId) {
        return departmentMapper.selectList(new LambdaQueryWrapper<OrgDepartmentEntity>()
                        .eq(OrgDepartmentEntity::getOrgId, orgId)
                        .orderByAsc(OrgDepartmentEntity::getSortNo)
                        .orderByAsc(OrgDepartmentEntity::getId))
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public OrgDepartment insert(Long tenantId, Long orgId, DepartmentSaveRequest request) {
        OrgDepartmentEntity dept = new OrgDepartmentEntity();
        dept.setTenantId(tenantId);
        dept.setOrgId(orgId);
        dept.setParentId(request.getParentId() == null ? 0L : request.getParentId());
        dept.setDeptName(request.getDeptName());
        dept.setSortNo(request.getSortNo() == null ? 0 : request.getSortNo());
        departmentMapper.insert(dept);
        return toDomain(dept);
    }

    @Override
    public OrgDepartment update(Long orgId, Long deptId, DepartmentSaveRequest request) {
        OrgDepartmentEntity dept = requireDepartment(orgId, deptId);
        dept.setParentId(request.getParentId() == null ? dept.getParentId() : request.getParentId());
        dept.setDeptName(request.getDeptName());
        if (request.getSortNo() != null) {
            dept.setSortNo(request.getSortNo());
        }
        departmentMapper.updateById(dept);
        return toDomain(dept);
    }

    @Override
    public void delete(Long orgId, Long deptId) {
        OrgDepartmentEntity dept = requireDepartment(orgId, deptId);
        departmentMapper.deleteById(dept.getId());
    }

    private OrgDepartmentEntity requireDepartment(Long orgId, Long deptId) {
        OrgDepartmentEntity dept = departmentMapper.selectById(deptId);
        if (dept == null || !orgId.equals(dept.getOrgId())) {
            throw Errors.of(PlatformErrorCode.ORG_DEPARTMENT_NOT_FOUND);
        }
        return dept;
    }

    private OrgDepartment toDomain(OrgDepartmentEntity entity) {
        return new OrgDepartment(
                entity.getId(),
                entity.getTenantId(),
                entity.getOrgId(),
                entity.getParentId(),
                entity.getDeptName(),
                entity.getSortNo(),
                entity.getCreateTime(),
                entity.getUpdateTime()
        );
    }
}
