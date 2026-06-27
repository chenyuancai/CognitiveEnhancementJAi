package cn.cyc.ai.cog.platform.iam.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 租户（映射 qz_iam_tenant）。
 */
@Data
@TableName("qz_iam_tenant")
public class SysTenantEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String tenantCode;
    private String tenantName;
    private String segment;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;

    @Version
    private Integer version;
}
