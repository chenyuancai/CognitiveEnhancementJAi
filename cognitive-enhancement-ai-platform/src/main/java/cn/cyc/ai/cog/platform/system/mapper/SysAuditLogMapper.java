package cn.cyc.ai.cog.platform.system.mapper;

import cn.cyc.ai.cog.platform.system.entity.AuditLogEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SysAuditLogMapper extends BaseMapper<AuditLogEntity> {
}
