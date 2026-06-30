package cn.cyc.ai.cog.platform.iam.dto;

import lombok.Data;

/**
 * 用户管理后台结果
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class UserAdminResult {

    /** 主键 ID */
    private String id;
    /** username。 */
    private String username;
    /** nickname。 */
    private String nickname;
    /** 邮箱。 */
    private String email;
    /** 手机号。 */
    private String phone;
    /** avatar地址。 */
    private String avatarUrl;
    /** 状态。 */
    private String status;
    /** 用户类型。 */
    private String userType;
    /** 等级编码。 */
    private String levelCode;
    /** 账户ID */
    private String accountId;
    /** 租户 ID */
    private String tenantId;
}
