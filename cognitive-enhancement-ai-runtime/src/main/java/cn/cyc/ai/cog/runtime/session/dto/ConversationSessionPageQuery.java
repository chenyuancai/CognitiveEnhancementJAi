package cn.cyc.ai.cog.runtime.session.dto;

/**
 * 会话分页查询参数。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public class ConversationSessionPageQuery {

    /** current。 */
    private long current = 1;
    /** 大小。 */
    private long size = 10;
    /** 用户 ID */
    private String userId;
    /** 能力编码。 */
    private String capabilityCode;
    /** 状态。 */
    private String status;

    /**
     * 获取Current。
     * @return Current
     */
    public long getCurrent() {
        return current < 1 ? 1 : current;
    }

    /**
     * 设置Current。
     *
     * @param current current
     */
    public void setCurrent(long current) {
        this.current = current;
    }

    /**
     * 获取大小。
     * @return 大小
     */
    public long getSize() {
        if (size < 1) {
            return 10;
        }
        return Math.min(size, 50);
    }

    /**
     * 设置大小。
     *
     * @param size 大小
     */
    public void setSize(long size) {
        this.size = size;
    }

    /**
     * 获取用户ID。
     * @return 用户ID
     */
    public String getUserId() {
        return userId;
    }

    /**
     * 设置用户ID。
     *
     * @param userId 用户 ID
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * 获取能力编码。
     * @return 能力编码
     */
    public String getCapabilityCode() {
        return capabilityCode;
    }

    /**
     * 设置能力编码。
     *
     * @param capabilityCode 能力编码
     */
    public void setCapabilityCode(String capabilityCode) {
        this.capabilityCode = capabilityCode;
    }

    /**
     * 获取状态。
     * @return 状态
     */
    public String getStatus() {
        return status;
    }

    /**
     * 设置状态。
     *
     * @param status 状态
     */
    public void setStatus(String status) {
        this.status = status;
    }
}
