package cn.cyc.ai.cog.admin.org.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Organization视图对象
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class OrganizationVO {

    /** 主键 ID */
    private Long id;
    /** 租户 ID */
    private Long tenantId;
    /** 账户ID */
    private Long accountId;
    /** org类型。 */
    private String orgType;
    /** org名称。 */
    private String orgName;
    /** unifiedSocial编码。 */
    private String unifiedSocialCode;
    /** seat限制。 */
    private Integer seatLimit;
    /** contact名称。 */
    private String contactName;
    /** contact手机号。 */
    private String contactPhone;
    /** 创建时间 */
    private LocalDateTime createTime;
    /** 更新时间 */
    private LocalDateTime updateTime;
}
