package cn.cyc.ai.cog.admin.content.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 内容ImportJob视图对象
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class ContentImportJobVO {

    /** 主键 ID */
    private Long id;
    /** 租户 ID */
    private Long tenantId;
    /** 文件名称。 */
    private String fileName;
    /** 文件地址。 */
    private String fileUrl;
    /** 状态。 */
    private String status;
    /** 总数数量。 */
    private Integer totalCount;
    /** 成功数量。 */
    private Integer successCount;
    /** fail数量。 */
    private Integer failCount;
    /** 结果JSON。 */
    private String resultJson;
    /** 创建人 ID */
    private Long createBy;
    /** 创建时间 */
    private LocalDateTime createTime;
    /** 更新时间 */
    private LocalDateTime updateTime;
}
