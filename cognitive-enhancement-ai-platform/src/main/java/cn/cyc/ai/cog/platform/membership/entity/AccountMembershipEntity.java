package cn.cyc.ai.cog.platform.membership.entity;

import cn.cyc.ai.cog.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 账户会员态（映射 qz_mbr_account）。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("qz_mbr_account")
public class AccountMembershipEntity extends BaseEntity {

    /** 账户ID */
    private Long accountId;
    /** 等级ID */
    private Long levelId;
    /** 等级编码。 */
    private String levelCode;
    /** expireAt。 */
    private LocalDateTime expireAt;
    /** 来源。 */
    private String source;
}
