package cn.cyc.ai.cog.runtime.file.mapper;

import cn.cyc.ai.cog.runtime.file.entity.FileUploadRecordEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 文件上传记录 Mapper。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Mapper
public interface FileUploadRecordMapper extends BaseMapper<FileUploadRecordEntity> {
}
