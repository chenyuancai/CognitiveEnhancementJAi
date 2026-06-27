package cn.cyc.ai.cog.runtime.audit.mapper;

import cn.cyc.ai.cog.runtime.audit.entity.AuditLogEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 审计日志 Mapper。
 *
 * @author cyc
 */
@Mapper
public interface AuditLogMapper extends BaseMapper<AuditLogEntity> {
}
