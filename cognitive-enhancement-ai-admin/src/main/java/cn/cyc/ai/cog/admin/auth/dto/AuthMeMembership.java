package cn.cyc.ai.cog.admin.auth.dto;

import lombok.Data;

@Data
public class AuthMeMembership {
    private String levelCode;
    private String levelName;
    private String expireAt;
}
