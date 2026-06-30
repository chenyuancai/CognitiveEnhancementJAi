package cn.cyc.ai.cog.base.api.file;

/**
 * 文件分页查询。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public class FilePageQuery {

    /** current。 */
    private Integer current = 1;

    /** 大小。 */
    private Integer size = 20;

    /** 租户 ID */
    private Long tenantId;

    /** biz编码。 */
    private String bizCode;

    /** 状态。 */
    private FileStatus status;

    /** 关键词。 */
    private String keyword;

    /**
     * 获取Current。
     * @return Current
     */
    public Integer getCurrent() {
        return current;
    }

    /**
     * 设置Current。
     *
     * @param current current
     */
    public void setCurrent(Integer current) {
        this.current = current;
    }

    /**
     * 获取大小。
     * @return 大小
     */
    public Integer getSize() {
        return size;
    }

    /**
     * 设置大小。
     *
     * @param size 大小
     */
    public void setSize(Integer size) {
        this.size = size;
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
     * 获取关键词。
     * @return 关键词
     */
    public String getKeyword() {
        return keyword;
    }

    /**
     * 设置关键词。
     *
     * @param keyword 关键词
     */
    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}
