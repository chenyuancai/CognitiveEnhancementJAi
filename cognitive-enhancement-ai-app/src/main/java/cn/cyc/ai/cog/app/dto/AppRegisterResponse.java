package cn.cyc.ai.cog.app.dto;

import lombok.Data;

/**
 * C 端注册响应。
 */
@Data
public class AppRegisterResponse {

    private Long userId;
    private String username;
    private String nickname;
}
