package cn.cyc.ai.cog.common.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 管理后台实体基类（借鉴 zcloud BaseEntity），含审计字段与逻辑删除。
 *
 * <p>新建的管理后台领域实体统一继承本类；历史 runtime/center 实体保持原基类不变。</p>
 *
 * @author cyc
 */
@Data
public class BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 租户 ID，用于数据隔离。 */
    @TableField(value = "tenant_id", fill = FieldFill.INSERT)
    private Long tenantId;

    @TableField(value = "create_by", fill = FieldFill.INSERT)
    private Long createBy;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_by", fill = FieldFill.INSERT_UPDATE)
    private Long updateBy;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /** 逻辑删除标记：0 未删除，1 已删除。 */
    @TableLogic
    @TableField(value = "deleted")
    private Integer deleted;

    /** 乐观锁版本号（额度/订单等热表使用）。 */
    @Version
    @TableField(value = "version")
    private Integer version = 0;
}
