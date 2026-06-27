package cn.cyc.ai.cog.platform.account.entity;

import cn.cyc.ai.cog.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商业账户（映射 qz_acct_account）。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("qz_acct_account")
public class AccountEntity extends BaseEntity {

    private String accountType;
    private String segment;
    private String displayName;
    private Long ownerUserId;
    private String status;
}
