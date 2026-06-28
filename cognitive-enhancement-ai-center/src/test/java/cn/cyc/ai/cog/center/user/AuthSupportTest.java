package cn.cyc.ai.cog.center.user;

import cn.cyc.ai.cog.common.exception.ServiceException;
import cn.cyc.ai.cog.runtime.security.JwtProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AuthSupportTest {

    private JwtProperties jwtProperties;
    private AuthSupport authSupport;

    @BeforeEach
    void setUp() {
        jwtProperties = new JwtProperties();
        jwtProperties.setAuthEnabled(true);
        authSupport = new AuthSupport(jwtProperties);
    }

    @Test
    void requireRole_shouldPassWhenRolePresent() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute(AuthSupport.ATTR_USER_ID, 1L);
        request.setAttribute(AuthSupport.ATTR_ROLES, List.of("ADMIN", "USER"));

        assertDoesNotThrow(() -> authSupport.requireRole(request, "ADMIN"));
    }

    @Test
    void requireRole_shouldRejectWhenRoleMissing() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute(AuthSupport.ATTR_USER_ID, 2L);
        request.setAttribute(AuthSupport.ATTR_ROLES, List.of("USER"));

        ServiceException ex = assertThrows(ServiceException.class,
                () -> authSupport.requireRole(request, "ADMIN"));
        assertEquals("A0403", ex.getCode());
    }

    @Test
    void requireRole_shouldBypassWhenAuthDisabled() {
        jwtProperties.setAuthEnabled(false);
        MockHttpServletRequest request = new MockHttpServletRequest();

        assertDoesNotThrow(() -> authSupport.requireRole(request, "ADMIN"));
    }
}
