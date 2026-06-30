package cn.cyc.ai.cog.admin.security;

import cn.cyc.ai.cog.common.exception.Errors;
import cn.cyc.ai.cog.common.exception.PlatformErrorCode;
import cn.cyc.ai.cog.common.constant.CommonConstants;
import cn.cyc.ai.cog.common.context.AuthUser;
import cn.cyc.ai.cog.common.context.UserContext;
import cn.cyc.ai.cog.common.exception.ServiceException;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.aspectj.lang.JoinPoint;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * 权限点校验切面：解析 {@link RequirePermission} 并基于 {@link UserContext} 校验。
 * <p>方法级注解优先于类级注解；{@code ADMIN} 角色放行。</p>
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Aspect
@Component
public class PermissionAspect {

    @Before("@within(cn.cyc.ai.cog.admin.security.RequirePermission) "
            + "|| @annotation(cn.cyc.ai.cog.admin.security.RequirePermission)")
    /**
     * 执行check权限。
     *
     * @param joinPoint joinPoint
     */
    public void checkPermission(JoinPoint joinPoint) {
        RequirePermission required = resolveAnnotation(joinPoint);
        if (required == null) {
            return;
        }

        AuthUser user = UserContext.get();
        if (user == null) {
            throw Errors.of(PlatformErrorCode.UNAUTHORIZED);
        }
        // 超级管理员放行
        if (user.hasRole(CommonConstants.ROLE_ADMIN)) {
            return;
        }

        String[] codes = required.value();
        boolean pass = required.logical() == RequirePermission.Logical.OR
                ? Arrays.stream(codes).anyMatch(user::hasAuthority)
                : Arrays.stream(codes).allMatch(user::hasAuthority);
        if (!pass) {
            throw Errors.of(PlatformErrorCode.PERMISSION_DENIED, "缺少权限点：" + String.join(",", codes));
        }
    }

    /** 方法级注解优先，其次类级注解。 */
    private RequirePermission resolveAnnotation(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RequirePermission methodAnno = AnnotatedElementUtils.findMergedAnnotation(method, RequirePermission.class);
        if (methodAnno != null) {
            return methodAnno;
        }
        return AnnotatedElementUtils.findMergedAnnotation(joinPoint.getTarget().getClass(), RequirePermission.class);
    }
}
