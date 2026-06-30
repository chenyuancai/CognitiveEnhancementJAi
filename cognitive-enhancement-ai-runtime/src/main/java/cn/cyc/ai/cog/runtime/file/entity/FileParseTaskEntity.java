package cn.cyc.ai.cog.runtime.file.entity;

import cn.cyc.ai.cog.runtime.base.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;

/**
 * 文件解析任务实体。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("qz_rt_file_parse_task")
public class FileParseTaskEntity extends BaseEntity {

    /**
     * 租户 ID。
     */
    private Long tenantId;

    /**
     * 任务 ID。
     */
    private String taskId;

    /**
     * 文件 ID。
     */
    private String fileId;

    /**
     * 任务状态。
     */
    private String status;

    /**
     * 解析结果 JSON。
     */
    private String parseResultJson;

    /**
     * 错误信息。
     */
    private String errorMessage;

    /**
     * 开始时间。
     */
    private Instant startedAt;

    /**
     * 结束时间。
     */
    private Instant finishedAt;

    /**
     * 记录时间。
     */
    private Instant recordedAt;
}
