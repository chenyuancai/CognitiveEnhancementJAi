package cn.cyc.ai.cog.platform.iam.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * C 端用户注册请求。
 */
@Data
public class UserRegisterRequest {

    /** 注册方式：USERNAME / PHONE / EMAIL。 */
    @NotBlank
    private String mode;

    private String username;
    private String phone;
    private String email;

    @NotBlank
    private String password;

    private String nickname;
}
