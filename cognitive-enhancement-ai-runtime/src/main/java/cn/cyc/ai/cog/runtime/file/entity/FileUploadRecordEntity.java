package cn.cyc.ai.cog.runtime.file.entity;

import cn.cyc.ai.cog.runtime.base.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;

/**
 * 文件上传记录实体。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("qz_rt_file_upload_record")
public class FileUploadRecordEntity extends BaseEntity {

    /**
     * 租户 ID。
     */
    private Long tenantId;

    /**
     * 文件 ID。
     */
    private String fileId;

    /**
     * 文件名。
     */
    private String fileName;

    /**
     * 内容类型。
     */
    private String contentType;

    /**
     * 文件大小（字节）。
     */
    private Long sizeBytes;

    /**
     * 存储路径。
     */
    private String storagePath;

    /**
     * 校验和。
     */
    private String checksum;

    /**
     * 上传状态。
     */
    private String status;

    /**
     * 记录时间。
     */
    private Instant recordedAt;
}
