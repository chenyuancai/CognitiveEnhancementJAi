package cn.cyc.ai.cog.center.user;

import cn.cyc.ai.cog.runtime.security.JwtUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.servlet.http.HttpServletRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 用户管理 REST 接口。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Tag(name = "用户管理", description = "用户注册、登录与用户列表查询（含 ADMIN 权限校验）")
@RestController
@RequestMapping("/api/users")
public class UserController {

    /** 用户服务。 */
    private final UserService userService;
    /** jwtUtil。 */
    private final JwtUtil jwtUtil;
    /** 认证支持。 */
    private final AuthSupport authSupport;

    /**
     * 创建用户接口。
     *
     * @param userService 用户服务
     * @param jwtUtil jwtUtil
     * @param authSupport 认证支持
     */
    public UserController(UserService userService, JwtUtil jwtUtil, AuthSupport authSupport) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.authSupport = authSupport;
    }

    /**
     * 用户注册。
     */
    @Operation(summary = "用户注册（Users API）", description = "兼容路径 POST /api/users/register，注册后返回 Token。")
    @SecurityRequirements()
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRegisterRequest request) {
        UserResult result = userService.register(request);
        String token = jwtUtil.generateToken(result.id(), result.username(), result.roles());
        return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "注册成功",
                "data", Map.of("user", result, "token", token)
        ));
    }

    /**
     * 用户登录。
     */
    @Operation(summary = "用户登录（Users API）", description = "兼容路径 POST /api/users/login，登录后返回 Token。")
    @SecurityRequirements()
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginRequest request) {
        UserResult result = userService.login(request);
        String token = jwtUtil.generateToken(result.id(), result.username(), result.roles());
        return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "登录成功",
                "data", Map.of("user", result, "token", token)
        ));
    }

    /**
     * 根据 ID 查询用户。
     */
    @Operation(summary = "按 ID 查询用户", description = "需要 ADMIN 角色。根据用户主键查询用户详情。")
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id, HttpServletRequest request) {
        authSupport.requireRole(request, "ADMIN");
        UserResult result = userService.getById(id);
        if (result == null) {
            return ResponseEntity.status(404).body(Map.of(
                    "code", 404,
                    "message", "用户不存在"
            ));
        }
        return ResponseEntity.ok(Map.of(
                "code", 200,
                "data", result
        ));
    }

    /**
     * 分页查询用户列表。
     */
    @Operation(summary = "分页查询用户列表", description = "需要 ADMIN 角色。按 page/size 分页返回用户列表。")
    @GetMapping
    public ResponseEntity<?> listUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest request
    ) {
        authSupport.requireRole(request, "ADMIN");
        Page<UserResult> result = userService.listUsers(page, size);
        return ResponseEntity.ok(Map.of(
                "code", 200,
                "data", result
        ));
    }
}
