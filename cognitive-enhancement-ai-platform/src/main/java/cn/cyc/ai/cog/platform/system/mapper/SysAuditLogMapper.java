package cn.cyc.ai.cog.platform.system.mapper;

import cn.cyc.ai.cog.platform.system.entity.AuditLogEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * SysAuditLog数据访问 Mapper
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Mapper
public interface SysAuditLogMapper extends BaseMapper<AuditLogEntity> {
}
