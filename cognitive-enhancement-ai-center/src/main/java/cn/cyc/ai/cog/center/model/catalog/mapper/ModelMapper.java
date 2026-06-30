package cn.cyc.ai.cog.center.model.catalog.mapper;

import cn.cyc.ai.cog.center.model.catalog.entity.ModelEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 模型数据访问 Mapper
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Mapper
public interface ModelMapper extends BaseMapper<ModelEntity> {
}
