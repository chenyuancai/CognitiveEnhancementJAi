package cn.cyc.ai.cog.platform.billing.mapper;

import cn.cyc.ai.cog.platform.billing.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单 Mapper。
 *
 * @author cyc
 */
@Mapper
public interface OrderMapper extends BaseMapper<OrderEntity> {
}
