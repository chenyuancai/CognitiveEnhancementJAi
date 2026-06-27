package cn.cyc.ai.cog.base.dict.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 字典项保存请求。
 */
@Data
public class DictItemSaveRequest {

    private Long id;

    private String bizCode;

    private Long tenantId;

    @NotNull(message = "字典类型 id 不能为空")
    private Long typeId;

    /** 顶级项传 0 或 null */
    private Long parentId;

    @NotBlank(message = "字典项值不能为空")
    private String value;

    @NotBlank(message = "字典项标签不能为空")
    private String label;

    private String enLabel;

    private String remark;

    private Integer sort = 0;

    private Boolean status = Boolean.TRUE;
}
