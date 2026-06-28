package cn.cyc.ai.cog.admin.system.audit;

import cn.cyc.ai.cog.api.response.ApiResponse;
import cn.cyc.ai.cog.platform.system.service.AuditLogService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class AdminAuditAspectTest {

    @Mock
    private AuditLogService auditLogService;

    private AdminAuditAspect adminAuditAspect;

    @BeforeEach
    void setUp() {
        adminAuditAspect = new AdminAuditAspect(auditLogService, new ObjectMapper());
    }

    @Test
    void shouldSerializeApiResponseAsValidJson() {
        String json = invokeToJsonSnapshot(ApiResponse.success("ok"));

        assertThat(json).startsWith("{");
        assertThat(json).contains("\"success\":true");
        assertThat(json).contains("\"data\":\"ok\"");
    }

    @Test
    void shouldSkipAuditForPostPageQuery() throws Exception {
        Method pageMethod = WriteController.class.getDeclaredMethod("page");
        boolean skip = invokeIsPageQueryPost(pageMethod);

        assertThat(skip).isTrue();
    }

    @Test
    void shouldAuditNormalPostCreate() throws Exception {
        Method createMethod = WriteController.class.getDeclaredMethod("create");
        boolean skip = invokeIsPageQueryPost(createMethod);

        assertThat(skip).isFalse();
    }

    private String invokeToJsonSnapshot(Object value) {
        return (String) ReflectionTestUtils.invokeMethod(adminAuditAspect, "toJsonSnapshot", value);
    }

    private boolean invokeIsPageQueryPost(Method method) {
        return Boolean.TRUE.equals(ReflectionTestUtils.invokeMethod(adminAuditAspect, "isPageQueryPost", method));
    }

    @RestController
    static class WriteController {

        @PostMapping
        ApiResponse<String> create() {
            return ApiResponse.success("ok");
        }

        @PostMapping("/page")
        ApiResponse<String> page() {
            return ApiResponse.success("page");
        }
    }
}
