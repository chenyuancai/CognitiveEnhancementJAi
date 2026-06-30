package cn.cyc.ai.cog.admin.system.audit;

import cn.cyc.ai.cog.platform.support.OperationRecordMessages;
import cn.cyc.ai.cog.platform.system.service.AuditLogService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * 管理后台写操作审计：POST/PUT/DELETE 控制器方法自动落库（含中文 message）。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Aspect
@Component
public class AdminAuditAspect {

    /** MAXJSONLENGTH。 */
    private static final int MAX_JSON_LENGTH = 2000;

    /** auditLog服务。 */
    private final AuditLogService auditLogService;
    /** JSON 序列化器 */
    private final ObjectMapper objectMapper;

    /**
     * 创建AdminAuditAspect。
     *
     * @param auditLogService auditLog服务
     * @param objectMapper JSON 序列化器
     */
    public AdminAuditAspect(AuditLogService auditLogService, ObjectMapper objectMapper) {
        this.auditLogService = auditLogService;
        this.objectMapper = objectMapper;
    }

    /**
     * 执行auditWrite。
     *
     * @param joinPoint joinPoint
     * @return 执行结果
     */
    @Around("@within(org.springframework.web.bind.annotation.RestController) && within(cn.cyc.ai.cog.admin..*)")
    public Object auditWrite(ProceedingJoinPoint joinPoint) throws Throwable {
        Method method = resolveTargetMethod(joinPoint);
        if (!isWriteMapping(method) || isPageQueryPost(method)) {
            return joinPoint.proceed();
        }
        String action = resolveAction(method);
        String resourceType = joinPoint.getTarget().getClass().getSimpleName();
        String message = resolveMessage(method, action, resourceType);
        Object result = joinPoint.proceed();
        auditLogService.record(action, resourceType, null, null, toJsonSnapshot(result), message);
        return result;
    }

    /**
     * 判断是否为WriteMapping。
     *
     * @param method method
     * @return 是否满足条件
     */
    private boolean isWriteMapping(Method method) {
        return method.isAnnotationPresent(PostMapping.class)
                || method.isAnnotationPresent(PutMapping.class)
                || method.isAnnotationPresent(DeleteMapping.class);
    }

    /** POST 分页/查询（如 /page、/query）为只读，不写审计。 */
    private boolean isPageQueryPost(Method method) {
        if (!method.isAnnotationPresent(PostMapping.class)) {
            return false;
        }
        PostMapping mapping = method.getAnnotation(PostMapping.class);
        return Arrays.stream(mapping.value()).anyMatch(this::isReadOnlyPostSegment)
                || Arrays.stream(mapping.path()).anyMatch(this::isReadOnlyPostSegment);
    }

    /**
     * 判断是否为ReadOnlyPostSegment。
     *
     * @param segment segment
     * @return 是否满足条件
     */
    private boolean isReadOnlyPostSegment(String segment) {
        if (!StringUtils.hasText(segment)) {
            return false;
        }
        String normalized = segment.startsWith("/") ? segment.substring(1) : segment;
        return "page".equals(normalized)
                || normalized.endsWith("/page")
                || "query".equals(normalized)
                || normalized.endsWith("/query");
    }

    /**
     * 执行resolveAction。
     *
     * @param method method
     * @return 执行结果
     */
    private String resolveAction(Method method) {
        if (method.isAnnotationPresent(DeleteMapping.class)) {
            return "DELETE";
        }
        if (method.isAnnotationPresent(PutMapping.class)) {
            return "UPDATE";
        }
        if (method.isAnnotationPresent(PostMapping.class)) {
            PostMapping mapping = method.getAnnotation(PostMapping.class);
            String path = firstPath(mapping);
            if (isUpdatePostPath(path)) {
                return "UPDATE";
            }
            return "CREATE";
        }
        return "UPDATE";
    }

    /**
     * 判断是否为更新Post路径。
     *
     * @param path 路径
     * @return 是否满足条件
     */
    private boolean isUpdatePostPath(String path) {
        if (!StringUtils.hasText(path)) {
            return false;
        }
        String normalized = path.startsWith("/") ? path.substring(1) : path;
        return normalized.contains("update")
                || normalized.endsWith("/status")
                || normalized.endsWith("/permissions")
                || normalized.endsWith("/tags")
                || normalized.endsWith("/level")
                || normalized.endsWith("/audit")
                || normalized.endsWith("/offline")
                || normalized.endsWith("/rollback")
                || normalized.endsWith("/grant")
                || normalized.endsWith("/adjust")
                || normalized.contains("/send");
    }

    /**
     * 执行first路径。
     *
     * @param mapping mapping
     * @return 执行结果
     */
    private String firstPath(PostMapping mapping) {
        if (mapping.value().length > 0) {
            return mapping.value()[0];
        }
        if (mapping.path().length > 0) {
            return mapping.path()[0];
        }
        return "";
    }

    /**
     * 执行resolve目标Method。
     *
     * @param joinPoint joinPoint
     * @return 执行结果
     */
    private Method resolveTargetMethod(ProceedingJoinPoint joinPoint) throws NoSuchMethodException {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        if (method.getDeclaringClass().isInterface()) {
            return joinPoint.getTarget().getClass()
                    .getDeclaredMethod(signature.getName(), method.getParameterTypes());
        }
        return method;
    }

    /**
     * 执行resolve消息。
     *
     * @param method method
     * @param action action
     * @param resourceType resource类型
     * @return 执行结果
     */
    private String resolveMessage(Method method, String action, String resourceType) {
        Operation operation = AnnotatedElementUtils.findMergedAnnotation(method, Operation.class);
        String summary = operation == null ? null : operation.summary();
        return OperationRecordMessages.audit(summary, action, resourceType);
    }

    /**
     * 转换为JSON快照。
     *
     * @param value 值
     * @return 转换结果
     */
    private String toJsonSnapshot(Object value) {
        if (value == null) {
            return null;
        }
        try {
            return truncate(objectMapper.writeValueAsString(value));
        } catch (JsonProcessingException ex) {
            return truncate(objectMapper.createObjectNode()
                    .put("serializationError", ex.getOriginalMessage())
                    .put("type", value.getClass().getName())
                    .toString());
        }
    }

    /**
     * 执行truncate。
     *
     * @param value 值
     * @return 执行结果
     */
    private String truncate(String value) {
        if (value == null) {
            return null;
        }
        return value.length() > MAX_JSON_LENGTH ? value.substring(0, MAX_JSON_LENGTH) : value;
    }
}
