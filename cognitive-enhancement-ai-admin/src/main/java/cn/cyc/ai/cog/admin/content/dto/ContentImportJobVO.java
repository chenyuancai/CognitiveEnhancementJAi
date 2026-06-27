package cn.cyc.ai.cog.admin.content.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ContentImportJobVO {

    private Long id;
    private Long tenantId;
    private String fileName;
    private String fileUrl;
    private String status;
    private Integer totalCount;
    private Integer successCount;
    private Integer failCount;
    private String resultJson;
    private Long createBy;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
