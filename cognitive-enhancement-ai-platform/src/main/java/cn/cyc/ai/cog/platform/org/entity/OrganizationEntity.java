package cn.cyc.ai.cog.platform.org.entity;

import cn.cyc.ai.cog.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Organization实体
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("qz_acct_org")
public class OrganizationEntity extends BaseEntity {

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
}
