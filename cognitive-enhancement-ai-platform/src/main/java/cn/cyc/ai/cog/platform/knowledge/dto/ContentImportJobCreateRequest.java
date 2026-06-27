package cn.cyc.ai.cog.platform.knowledge.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ContentImportJobCreateRequest {

    @NotBlank
    private String fileName;
    /** 已上传至 base 的文件 ID（与 fileUrl / fileContent 三选一）。 */
    private Long fileId;
    private String fileUrl;
    /** CSV 原文（与 fileUrl / fileId 三选一）。 */
    private String fileContent;
}
