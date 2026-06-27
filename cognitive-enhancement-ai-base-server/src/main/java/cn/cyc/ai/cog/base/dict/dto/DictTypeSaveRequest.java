package cn.cyc.ai.cog.base.dict.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 字典类型保存请求。
 */
@Data
public class DictTypeSaveRequest {

    private Long id;

    private String bizCode;

    private String shareScope;

    private Long tenantId;

    @NotBlank(message = "类型编码不能为空")
    private String code;

    @NotBlank(message = "类型名称不能为空")
    private String name;

    private String enName;

    private String description;

    private String remark;

    /** 默认启用 */
    private Boolean status = Boolean.TRUE;
}
