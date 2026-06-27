package cn.cyc.ai.cog.base.api.file;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Feign 字节流上传（Base64 正文，避免 Multipart 编解码差异）。
 */
public class FileUploadBytesRequest {

    @NotNull
    private Long tenantId;

    private String bizCode;

    @NotBlank
    private String fileName;

    private String contentType;

    /** Base64 编码的文件内容 */
    @NotBlank
    private String base64Content;

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String getBizCode() {
        return bizCode;
    }

    public void setBizCode(String bizCode) {
        this.bizCode = bizCode;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getBase64Content() {
        return base64Content;
    }

    public void setBase64Content(String base64Content) {
        this.base64Content = base64Content;
    }
}
