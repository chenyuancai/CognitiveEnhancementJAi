package cn.cyc.ai.cog.platform.org.mapper;

import cn.cyc.ai.cog.platform.org.entity.OrgMemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * OrgMember数据访问 Mapper
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Mapper
public interface OrgMemberMapper extends BaseMapper<OrgMemberEntity> {

    @Select("SELECT COUNT(*) FROM qz_acct_org_member WHERE org_id = #{orgId} AND status = 'ACTIVE' AND deleted = 0")
    long countActiveMembers(Long orgId);
}
