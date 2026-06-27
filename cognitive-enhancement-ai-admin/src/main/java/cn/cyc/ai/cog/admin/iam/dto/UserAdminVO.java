package cn.cyc.ai.cog.admin.iam.dto;

import lombok.Data;

@Data
public class UserAdminVO {

    private String id;
    private String username;
    private String nickname;
    private String email;
    private String phone;
    private String avatarUrl;
    private String status;
    /** 用户类型：ADMIN / CUSTOMER */
    private String userType;
    private String levelCode;
    private String accountId;
    private String tenantId;
}
