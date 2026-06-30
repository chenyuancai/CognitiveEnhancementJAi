package cn.cyc.ai.cog.platform.account.mapper;

import cn.cyc.ai.cog.platform.account.entity.AccountEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 账户数据访问 Mapper
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Mapper
public interface AccountMapper extends BaseMapper<AccountEntity> {
}
