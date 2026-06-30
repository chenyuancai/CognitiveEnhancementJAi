package cn.cyc.ai.cog.base.dict.entity;

import cn.cyc.ai.cog.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 基础字典项（映射 qz_base_dict_item）。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("qz_base_dict_item")
public class DictItemEntity extends BaseEntity {

    /** biz编码。 */
    private String bizCode;

    /** 类型ID */
    private Long typeId;

    /** parentID */
    private Long parentId;

    /** 值。 */
    @com.baomidou.mybatisplus.annotation.TableField("`value`")
    private String value;

    /** label。 */
    private String label;

    /** enLabel。 */
    private String enLabel;

    /** remark。 */
    private String remark;

    /** sort。 */
    private Integer sort;

    /** 1=启用，0=禁用 */
    private Integer status;
}
