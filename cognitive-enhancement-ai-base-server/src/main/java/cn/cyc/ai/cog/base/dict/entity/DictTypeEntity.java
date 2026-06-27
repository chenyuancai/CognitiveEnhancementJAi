package cn.cyc.ai.cog.base.dict.entity;

import cn.cyc.ai.cog.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 基础字典类型（映射 qz_base_dict_type）。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("qz_base_dict_type")
public class DictTypeEntity extends BaseEntity {

    private String bizCode;

    private Integer dictKind;

    private String shareScope;

    private String code;

    private String name;

    private String enName;

    private String description;

    private String remark;

    /** 1=启用，0=禁用 */
    private Integer status;
}
