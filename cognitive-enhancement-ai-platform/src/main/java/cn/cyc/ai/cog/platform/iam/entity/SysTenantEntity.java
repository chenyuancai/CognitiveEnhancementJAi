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
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
@TableName("qz_iam_tenant")
public class SysTenantEntity {

    /** 主键 ID */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 租户编码。 */
    private String tenantCode;
    /** 租户名称。 */
    private String tenantName;
    /** segment。 */
    private String segment;
    /** 状态。 */
    private String status;
    /** 创建时间 */
    private LocalDateTime createTime;
    /** 更新时间 */
    private LocalDateTime updateTime;

    /** 逻辑删除标记 */
    @TableLogic
    private Integer deleted;

    /** 版本号 */
    @Version
    private Integer version;
}
