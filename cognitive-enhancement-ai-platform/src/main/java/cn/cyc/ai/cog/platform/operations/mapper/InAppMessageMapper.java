package cn.cyc.ai.cog.platform.operations.mapper;

import cn.cyc.ai.cog.platform.operations.entity.InAppMessageEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * InC端消息数据访问 Mapper
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Mapper
public interface InAppMessageMapper extends BaseMapper<InAppMessageEntity> {
}
