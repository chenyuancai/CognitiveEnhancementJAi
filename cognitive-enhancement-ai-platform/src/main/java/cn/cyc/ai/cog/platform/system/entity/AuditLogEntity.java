package cn.cyc.ai.cog.platform.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("qz_sys_audit_log")
public class AuditLogEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long operatorId;
    private String operatorName;
    private String action;
    private String message;
    private String resourceType;
    private String resourceId;
    private String beforeJson;
    private String afterJson;
    private String ipAddress;
    private LocalDateTime createTime;
}
