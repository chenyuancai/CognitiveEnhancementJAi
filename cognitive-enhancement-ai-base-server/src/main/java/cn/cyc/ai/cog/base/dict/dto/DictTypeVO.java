package cn.cyc.ai.cog.base.dict.dto;

import lombok.Data;

/**
 * 字典类型视图。
 */
@Data
public class DictTypeVO {

    private Long id;

    private Long tenantId;

    private String bizCode;

    private Integer dictKind;

    private String shareScope;

    private String code;

    private String name;

    private String enName;

    private String description;

    private String remark;

    private Boolean status;
}
