package cn.cyc.ai.cog.app.dto;

import lombok.Data;

/**
 * C 端注册响应。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class AppRegisterResponse {

    /** 用户 ID */
    private Long userId;
    /** username。 */
    private String username;
    /** nickname。 */
    private String nickname;
}
