package cn.cyc.ai.cog.platform.iam.dto;

import lombok.Data;

@Data
public class UserAdminResult {

    private String id;
    private String username;
    private String nickname;
    private String email;
    private String phone;
    private String avatarUrl;
    private String status;
    private String userType;
    private String levelCode;
    private String accountId;
    private String tenantId;
}
