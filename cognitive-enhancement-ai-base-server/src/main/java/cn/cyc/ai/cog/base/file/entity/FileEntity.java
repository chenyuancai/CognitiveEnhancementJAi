package cn.cyc.ai.cog.base.file.entity;

import cn.cyc.ai.cog.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 基础文件元数据（映射 qz_base_file）。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("qz_base_file")
public class FileEntity extends BaseEntity {

    private String bizCode;

    private String originalName;

    private String storageName;

    private String storagePath;

    private String contentType;

    private Long sizeBytes;

    private String md5;

    /** 1=UNCONFIRMED 2=CONFIRMED */
    private Integer status;
}
