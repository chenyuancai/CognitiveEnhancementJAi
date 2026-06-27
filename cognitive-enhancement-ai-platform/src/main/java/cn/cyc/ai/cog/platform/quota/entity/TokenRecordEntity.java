package cn.cyc.ai.cog.platform.quota.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("qz_mbr_token_record")
public class TokenRecordEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long accountId;
    private Long memberUserId;
    private String recordType;
    private String bucket;
    private Long deltaAmount;
    private Long balanceAfter;
    private String bizType;
    private String bizId;
    private String idempotencyKey;
    private String message;
    private String remark;
    private LocalDateTime createTime;
}
