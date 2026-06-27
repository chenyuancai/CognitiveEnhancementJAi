package cn.cyc.ai.cog.platform.iam.dto;

import cn.cyc.ai.cog.common.page.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserPageQuery extends PageQuery {

    private String keyword;
    private String status;
    private String userType;
    private String levelCode;
}
