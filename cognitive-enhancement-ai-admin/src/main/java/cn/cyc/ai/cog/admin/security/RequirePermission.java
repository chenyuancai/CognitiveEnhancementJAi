package cn.cyc.ai.cog.admin.security;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 权限点校验注解：标注在 Controller 类或方法上，声明访问所需的权限点。
 * <p>由 {@link PermissionAspect} 拦截校验。拥有 {@code ADMIN} 角色的用户默认放行。</p>
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Documented
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequirePermission {

    /** 所需权限点编码，如 {@code system:role:update}。 */
    String[] value();

    /** 多个权限点的匹配逻辑。 */
    Logical logical() default Logical.AND;

    /**
     * Logical 枚举
     *
     * @author cyc
     * @date 2026/6/15 14:18
     */
    enum Logical {
        /** 需同时具备全部权限点。 */
        AND,
        /** 具备任一权限点即可。 */
        OR
    }
}
