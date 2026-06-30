package cn.cyc.ai.cog.runtime.file.mapper;

import cn.cyc.ai.cog.runtime.file.entity.FileParseTaskEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 文件解析任务 Mapper。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Mapper
public interface FileParseTaskMapper extends BaseMapper<FileParseTaskEntity> {
}
