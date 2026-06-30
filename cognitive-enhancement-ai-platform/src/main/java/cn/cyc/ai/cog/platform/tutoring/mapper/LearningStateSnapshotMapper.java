package cn.cyc.ai.cog.platform.tutoring.mapper;

import cn.cyc.ai.cog.platform.tutoring.entity.LearningStateSnapshotEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 学习状态快照 Mapper。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Mapper
public interface LearningStateSnapshotMapper extends BaseMapper<LearningStateSnapshotEntity> {
}
