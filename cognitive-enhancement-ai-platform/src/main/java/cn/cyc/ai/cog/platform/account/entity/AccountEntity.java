package cn.cyc.ai.cog.platform.account.entity;

import cn.cyc.ai.cog.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商业账户（映射 qz_acct_account）。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("qz_acct_account")
public class AccountEntity extends BaseEntity {

    /** 账户类型。 */
    private String accountType;
    /** segment。 */
    private String segment;
    /** display名称。 */
    private String displayName;
    /** owner用户ID */
    private Long ownerUserId;
    /** 状态。 */
    private String status;
}
