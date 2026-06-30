package cn.cyc.ai.cog.platform.knowledge.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 内容ImportJob创建请求
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class ContentImportJobCreateRequest {

    /** 文件名称。 */
    @NotBlank
    private String fileName;
    /** 已上传至 base 的文件 ID（与 fileUrl / fileContent 三选一）。 */
    private Long fileId;
    /** 文件地址。 */
    private String fileUrl;
    /** CSV 原文（与 fileUrl / fileId 三选一）。 */
    private String fileContent;
}
