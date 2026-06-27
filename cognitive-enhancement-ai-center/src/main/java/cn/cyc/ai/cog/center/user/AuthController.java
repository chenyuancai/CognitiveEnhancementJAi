package cn.cyc.ai.cog.center.user;

import cn.cyc.ai.cog.runtime.security.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 认证相关 REST 接口。
 *
 * @author cyc
 */
@Tag(name = "鉴权", description = "用户登录与注册，获取 JWT Token")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AuthController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * 用户登录。
     */
    @Operation(summary = "用户登录", description = "使用用户名和密码登录，返回 JWT Token 与用户信息。前端请将 Token 写入 Authorization 请求头。")
    @SecurityRequirements()
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginRequest request) {
        UserResult result = userService.login(request);
        String token = jwtUtil.generateToken(result.id(), result.username(), result.tenantCode(), result.roles());
        return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "登录成功",
                "data", Map.of("user", result, "token", token)
        ));
    }

    /**
     * 用户注册。
     */
    @Operation(summary = "用户注册", description = "注册新用户并自动返回 JWT Token。无需预先登录。")
    @SecurityRequirements()
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRegisterRequest request) {
        UserResult result = userService.register(request);
        String token = jwtUtil.generateToken(result.id(), result.username(), result.tenantCode(), result.roles());
        return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "注册成功",
                "data", Map.of("user", result, "token", token)
        ));
    }
}
