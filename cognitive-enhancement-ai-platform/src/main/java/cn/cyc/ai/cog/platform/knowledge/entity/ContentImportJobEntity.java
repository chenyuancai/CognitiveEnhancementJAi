package cn.cyc.ai.cog.platform.knowledge.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 内容ImportJob实体
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
@TableName("qz_kb_content_import_job")
public class ContentImportJobEntity {

    /** 主键 ID */
    @TableId(type = IdType.AUTO)
    private Long id;
    /** 租户 ID */
    private Long tenantId;
    /** 文件名称。 */
    private String fileName;
    /** 文件地址。 */
    private String fileUrl;
    /** 来源内容。 */
    private String sourceContent;
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
