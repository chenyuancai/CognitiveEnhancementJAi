package cn.cyc.ai.cog.platform.iam.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * C 端用户注册请求。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class UserRegisterRequest {

    /** 注册方式：USERNAME / PHONE / EMAIL。 */
    @NotBlank
    private String mode;

    /** username。 */
    private String username;
    /** 手机号。 */
    private String phone;
    /** 邮箱。 */
    private String email;

    /** 密码。 */
    @NotBlank
    private String password;

    /** nickname。 */
    private String nickname;
}
