package cn.cyc.ai.cog.runtime.security;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtUtilTest {

    @Test
    void shouldGenerateAndExtractTenantCode() {
        JwtProperties properties = new JwtProperties();
        properties.setSecret("01234567890123456789012345678901");
        JwtUtil jwtUtil = new JwtUtil(properties);

        String token = jwtUtil.generateToken(1L, "admin", "tenant-a", List.of("ADMIN"));

        assertTrue(jwtUtil.validateToken(token));
        assertEquals(1L, jwtUtil.extractUserId(token));
        assertEquals("admin", jwtUtil.extractUsername(token));
        assertEquals("tenant-a", jwtUtil.extractTenantCode(token));
        assertEquals(List.of("ADMIN"), jwtUtil.extractRoles(token));
    }
}
