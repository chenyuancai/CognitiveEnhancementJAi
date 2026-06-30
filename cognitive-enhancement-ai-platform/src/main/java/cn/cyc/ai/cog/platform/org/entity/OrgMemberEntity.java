package cn.cyc.ai.cog.platform.org.entity;

import cn.cyc.ai.cog.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * OrgMember实体
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("qz_acct_org_member")
public class OrgMemberEntity extends BaseEntity {

    /** orgID */
    private Long orgId;
    /** 用户 ID */
    private Long userId;
    /** deptID */
    private Long deptId;
    /** org角色。 */
    private String orgRole;
    /** 状态。 */
    private String status;
    /** joinedAt。 */
    private LocalDateTime joinedAt;
}
