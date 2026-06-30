package cn.cyc.ai.cog.admin.auth.dto;

import lombok.Data;

/**
 * AuthMeUser
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class AuthMeUser {
    /** 主键 ID */
    private String id;
    /** username。 */
    private String username;
    /** nickname。 */
    private String nickname;
    /** avatar地址。 */
    private String avatarUrl;
    /** 状态。 */
    private String status;
}
