package cn.cyc.ai.cog.admin.auth.dto;

import lombok.Data;

@Data
public class AuthMeAccount {
    private String id;
    private String accountType;
    private String segment;
    private String displayName;
}
