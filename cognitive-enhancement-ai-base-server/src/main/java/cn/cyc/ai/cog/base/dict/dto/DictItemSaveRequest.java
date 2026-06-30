package cn.cyc.ai.cog.base.dict.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 字典项保存请求。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class DictItemSaveRequest {

    /** 主键 ID */
    private Long id;

    /** biz编码。 */
    private String bizCode;

    /** 租户 ID */
    private Long tenantId;

    /** 类型ID */
    @NotNull(message = "字典类型 id 不能为空")
    private Long typeId;

    /** 顶级项传 0 或 null */
    private Long parentId;

    /** 值。 */
    @NotBlank(message = "字典项值不能为空")
    private String value;

    /** label。 */
    @NotBlank(message = "字典项标签不能为空")
    private String label;

    /** enLabel。 */
    private String enLabel;

    /** remark。 */
    private String remark;

    /** sort。 */
    private Integer sort = 0;

    /** 状态。 */
    private Boolean status = Boolean.TRUE;
}
