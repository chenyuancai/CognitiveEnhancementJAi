package cn.cyc.ai.cog.platform.membership.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("qz_mbr_membership_change_log")
public class MembershipChangeLogEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long accountId;
    private Long userId;
    private String fromLevelCode;
    private String toLevelCode;
    private String changeType;
    private String message;
    private Long orderId;
    private Long operatorId;
    private String remark;
    private LocalDateTime createTime;
}
