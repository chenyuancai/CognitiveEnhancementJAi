package cn.cyc.ai.cog.platform.knowledge.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("qz_kb_content_import_job")
public class ContentImportJobEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private String fileName;
    private String fileUrl;
    private String sourceContent;
    private String status;
    private Integer totalCount;
    private Integer successCount;
    private Integer failCount;
    private String resultJson;
    private Long createBy;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
