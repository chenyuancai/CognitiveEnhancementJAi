package cn.cyc.ai.cog.platform.org.entity;

import cn.cyc.ai.cog.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("qz_acct_org")
public class OrganizationEntity extends BaseEntity {

    private Long accountId;
    private String orgType;
    private String orgName;
    private String unifiedSocialCode;
    private Integer seatLimit;
    private String contactName;
    private String contactPhone;
}
