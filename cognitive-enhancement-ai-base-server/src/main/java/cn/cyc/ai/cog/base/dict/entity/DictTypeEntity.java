package cn.cyc.ai.cog.base.dict.entity;

import cn.cyc.ai.cog.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 基础字典类型（映射 qz_base_dict_type）。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("qz_base_dict_type")
public class DictTypeEntity extends BaseEntity {

    /** biz编码。 */
    private String bizCode;

    /** dictKind。 */
    private Integer dictKind;

    /** shareScope。 */
    private String shareScope;

    /** 编码。 */
    private String code;

    /** 名称。 */
    private String name;

    /** en名称。 */
    private String enName;

    /** 描述。 */
    private String description;

    /** remark。 */
    private String remark;

    /** 1=启用，0=禁用 */
    private Integer status;
}
