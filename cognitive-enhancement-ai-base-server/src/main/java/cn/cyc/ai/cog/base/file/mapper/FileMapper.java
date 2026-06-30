package cn.cyc.ai.cog.base.file.mapper;

import cn.cyc.ai.cog.base.file.entity.FileEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 文件数据访问 Mapper
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Mapper
public interface FileMapper extends BaseMapper<FileEntity> {
}
