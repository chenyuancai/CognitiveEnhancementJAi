package cn.cyc.ai.cog.admin.content.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Admin 导入工作流同步调试请求。
 */
@Data
public class AdminImportWorkflowDebugRequest {

    @NotBlank(message = "importBizType 不能为空")
    private String importBizType;

    @NotNull(message = "fileId 不能为空")
    private Long fileId;

    private String fileUrl;
    private String fileName;
    private String title;
    private Long tenantId;
    private Long userId;
    private Boolean aiEnhanced;
    private Boolean autoQuiz;
}
