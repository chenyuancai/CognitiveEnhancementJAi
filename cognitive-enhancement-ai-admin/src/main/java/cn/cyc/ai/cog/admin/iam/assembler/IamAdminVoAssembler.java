package cn.cyc.ai.cog.admin.iam.assembler;

import cn.cyc.ai.cog.admin.iam.dto.TenantVO;
import cn.cyc.ai.cog.platform.iam.domain.Tenant;
import org.springframework.stereotype.Component;

/**
 * 管理端 IAM 域领域对象 → VO 转换器。
 */
@Component
public class IamAdminVoAssembler {

    /**
     * 租户领域对象转 VO。
     *
     * @param tenant 租户领域对象
     * @return 租户 VO
     */
    public TenantVO toTenantVo(Tenant tenant) {
        TenantVO vo = new TenantVO();
        vo.setId(tenant.id());
        vo.setTenantCode(tenant.tenantCode());
        vo.setTenantName(tenant.tenantName());
        vo.setSegment(tenant.segment());
        vo.setStatus(tenant.status());
        vo.setCreateTime(tenant.createTime());
        vo.setUpdateTime(tenant.updateTime());
        return vo;
    }
}
