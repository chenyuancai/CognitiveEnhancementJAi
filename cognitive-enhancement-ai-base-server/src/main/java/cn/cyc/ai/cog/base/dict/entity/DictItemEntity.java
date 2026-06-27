package cn.cyc.ai.cog.base.dict.entity;

import cn.cyc.ai.cog.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 基础字典项（映射 qz_base_dict_item）。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("qz_base_dict_item")
public class DictItemEntity extends BaseEntity {

    private String bizCode;

    private Long typeId;

    private Long parentId;

    @com.baomidou.mybatisplus.annotation.TableField("`value`")
    private String value;

    private String label;

    private String enLabel;

    private String remark;

    private Integer sort;

    /** 1=启用，0=禁用 */
    private Integer status;
}
