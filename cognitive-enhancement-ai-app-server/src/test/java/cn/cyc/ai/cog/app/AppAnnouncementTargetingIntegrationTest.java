package cn.cyc.ai.cog.app;

import cn.cyc.ai.cog.common.jwt.SecurityConstants;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("app-it")
@TestPropertySource(properties = {
        "cog.app.dev-auth-bypass=false",
        "cog.app.trust-gateway-headers=true"
})
class AppAnnouncementTargetingIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnAllAudienceAnnouncementForFreeUser() throws Exception {
        mockMvc.perform(get("/api/app/ops/announcements")
                        .header(SecurityConstants.HEADER_USER_ID, "1")
                        .header(SecurityConstants.HEADER_TENANT_ID, "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].title", is("系统公告")));
    }

    @Test
    void shouldReturnProAndAllAnnouncementsForProUser() throws Exception {
        mockMvc.perform(get("/api/app/ops/announcements")
                        .header(SecurityConstants.HEADER_USER_ID, "2")
                        .header(SecurityConstants.HEADER_TENANT_ID, "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", hasSize(3)));
    }

    @Test
    void shouldReturnUserGroupAnnouncementForTargetUser() throws Exception {
        mockMvc.perform(get("/api/app/ops/announcements")
                        .header(SecurityConstants.HEADER_USER_ID, "2")
                        .header(SecurityConstants.HEADER_TENANT_ID, "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[?(@.title == '定向用户公告')]").exists());
    }
}
