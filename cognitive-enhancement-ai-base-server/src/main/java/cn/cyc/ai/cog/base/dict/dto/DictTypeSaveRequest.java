package cn.cyc.ai.cog.base.dict.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 字典类型保存请求。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class DictTypeSaveRequest {

    /** 主键 ID */
    private Long id;

    /** biz编码。 */
    private String bizCode;

    /** shareScope。 */
    private String shareScope;

    /** 租户 ID */
    private Long tenantId;

    /** 编码。 */
    @NotBlank(message = "类型编码不能为空")
    private String code;

    /** 名称。 */
    @NotBlank(message = "类型名称不能为空")
    private String name;

    /** en名称。 */
    private String enName;

    /** 描述。 */
    private String description;

    /** remark。 */
    private String remark;

    /** 默认启用 */
    private Boolean status = Boolean.TRUE;
}
