package cn.cyc.ai.cog.platform.tutoring.mapper;

import cn.cyc.ai.cog.platform.tutoring.entity.MistakeRecordEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 错题记录 Mapper。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Mapper
public interface MistakeRecordMapper extends BaseMapper<MistakeRecordEntity> {
}
