package cn.cyc.ai.cog.base.api.file;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Feign 字节流上传（Base64 正文，避免 Multipart 编解码差异）。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public class FileUploadBytesRequest {

    /** 租户 ID */
    @NotNull
    private Long tenantId;

    /** biz编码。 */
    private String bizCode;

    /** 文件名称。 */
    @NotBlank
    private String fileName;

    /** 内容类型。 */
    private String contentType;

    /** Base64 编码的文件内容 */
    @NotBlank
    private String base64Content;

    /**
     * 获取租户ID。
     * @return 租户ID
     */
    public Long getTenantId() {
        return tenantId;
    }

    /**
     * 设置租户ID。
     *
     * @param tenantId 租户 ID
     */
    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    /**
     * 获取Biz编码。
     * @return Biz编码
     */
    public String getBizCode() {
        return bizCode;
    }

    /**
     * 设置Biz编码。
     *
     * @param bizCode biz编码
     */
    public void setBizCode(String bizCode) {
        this.bizCode = bizCode;
    }

    /**
     * 获取文件名称。
     * @return 文件名称
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * 设置文件名称。
     *
     * @param fileName 文件名称
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * 获取内容类型。
     * @return 内容类型
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * 设置内容类型。
     *
     * @param contentType 内容类型
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     * 获取Base64Content。
     * @return Base64Content
     */
    public String getBase64Content() {
        return base64Content;
    }

    /**
     * 设置Base64Content。
     *
     * @param base64Content base64Content
     */
    public void setBase64Content(String base64Content) {
        this.base64Content = base64Content;
    }
}
