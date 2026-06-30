package cn.cyc.ai.cog.base.file.entity;

import cn.cyc.ai.cog.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 基础文件元数据（映射 qz_base_file）。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("qz_base_file")
public class FileEntity extends BaseEntity {

    /** biz编码。 */
    private String bizCode;

    /** original名称。 */
    private String originalName;

    /** storage名称。 */
    private String storageName;

    /** storage路径。 */
    private String storagePath;

    /** 内容类型。 */
    private String contentType;

    /** 大小Bytes。 */
    private Long sizeBytes;

    /** md5。 */
    private String md5;

    /** 1=UNCONFIRMED 2=CONFIRMED */
    private Integer status;
}
