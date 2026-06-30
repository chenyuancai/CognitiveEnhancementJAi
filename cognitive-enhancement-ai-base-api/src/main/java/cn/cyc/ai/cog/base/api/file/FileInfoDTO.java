package cn.cyc.ai.cog.base.api.file;

import java.time.LocalDateTime;

/**
 * 文件元数据（上传结果 / 查询详情）。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public class FileInfoDTO {

    /** 主键 ID */
    private Long id;

    /** 租户 ID */
    private Long tenantId;

    /** biz编码。 */
    private String bizCode;

    /** original名称。 */
    private String originalName;

    /** 内容类型。 */
    private String contentType;

    /** 大小Bytes。 */
    private Long sizeBytes;

    /** md5。 */
    private String md5;

    /** storage路径。 */
    private String storagePath;

    /** 经网关访问的下载路径，如 /api/base/files/{id}/download */
    private String accessPath;

    /** 状态。 */
    private FileStatus status;

    /** 创建时间 */
    private LocalDateTime createTime;

    /**
     * 获取ID。
     * @return ID
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置ID。
     *
     * @param id 主键 ID
     */
    public void setId(Long id) {
        this.id = id;
    }

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
     * 获取Original名称。
     * @return Original名称
     */
    public String getOriginalName() {
        return originalName;
    }

    /**
     * 设置Original名称。
     *
     * @param originalName original名称
     */
    public void setOriginalName(String originalName) {
        this.originalName = originalName;
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
     * 获取大小Bytes。
     * @return 大小Bytes
     */
    public Long getSizeBytes() {
        return sizeBytes;
    }

    /**
     * 设置大小Bytes。
     *
     * @param sizeBytes 大小Bytes
     */
    public void setSizeBytes(Long sizeBytes) {
        this.sizeBytes = sizeBytes;
    }

    /**
     * 获取Md5。
     * @return Md5
     */
    public String getMd5() {
        return md5;
    }

    /**
     * 设置Md5。
     *
     * @param md5 md5
     */
    public void setMd5(String md5) {
        this.md5 = md5;
    }

    /**
     * 获取Storage路径。
     * @return Storage路径
     */
    public String getStoragePath() {
        return storagePath;
    }

    /**
     * 设置Storage路径。
     *
     * @param storagePath storage路径
     */
    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
    }

    /**
     * 获取Access路径。
     * @return Access路径
     */
    public String getAccessPath() {
        return accessPath;
    }

    /**
     * 设置Access路径。
     *
     * @param accessPath access路径
     */
    public void setAccessPath(String accessPath) {
        this.accessPath = accessPath;
    }

    /**
     * 获取状态。
     * @return 状态
     */
    public FileStatus getStatus() {
        return status;
    }

    /**
     * 设置状态。
     *
     * @param status 状态
     */
    public void setStatus(FileStatus status) {
        this.status = status;
    }

    /**
     * 获取创建时间。
     * @return 创建时间
     */
    public LocalDateTime getCreateTime() {
        return createTime;
    }

    /**
     * 设置创建时间。
     *
     * @param createTime 创建时间
     */
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
