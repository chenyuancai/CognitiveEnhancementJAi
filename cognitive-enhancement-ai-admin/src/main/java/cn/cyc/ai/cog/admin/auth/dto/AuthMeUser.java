package cn.cyc.ai.cog.admin.auth.dto;

import lombok.Data;

@Data
public class AuthMeUser {
    private String id;
    private String username;
    private String nickname;
    private String avatarUrl;
    private String status;
}
