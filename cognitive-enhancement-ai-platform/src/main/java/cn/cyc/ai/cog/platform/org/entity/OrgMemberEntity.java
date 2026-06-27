package cn.cyc.ai.cog.platform.org.entity;

import cn.cyc.ai.cog.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("qz_acct_org_member")
public class OrgMemberEntity extends BaseEntity {

    private Long orgId;
    private Long userId;
    private Long deptId;
    private String orgRole;
    private String status;
    private LocalDateTime joinedAt;
}
