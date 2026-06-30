package cn.cyc.ai.cog.platform.membership.mapper;

import cn.cyc.ai.cog.platform.membership.entity.AccountMembershipEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 账户会员数据访问 Mapper
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Mapper
public interface AccountMembershipMapper extends BaseMapper<AccountMembershipEntity> {
}
