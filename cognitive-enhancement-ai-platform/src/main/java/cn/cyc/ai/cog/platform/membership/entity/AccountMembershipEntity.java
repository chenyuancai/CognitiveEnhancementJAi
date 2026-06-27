package cn.cyc.ai.cog.platform.membership.entity;

import cn.cyc.ai.cog.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 账户会员态（映射 qz_mbr_account）。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("qz_mbr_account")
public class AccountMembershipEntity extends BaseEntity {

    private Long accountId;
    private Long levelId;
    private String levelCode;
    private LocalDateTime expireAt;
    private String source;
}
