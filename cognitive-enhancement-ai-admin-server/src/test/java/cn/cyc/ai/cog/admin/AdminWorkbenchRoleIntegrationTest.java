package cn.cyc.ai.cog.admin;

import cn.cyc.ai.cog.common.jwt.SecurityConstants;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"admin-it", "prod"})
@TestPropertySource(properties = {
        "cog.admin.dev-auth-bypass=false",
        "cog.admin.trust-gateway-headers=true",
        "cog.jwt.trust-gateway-headers=true",
        "cog.persistence.enabled=false"
})
class AdminWorkbenchRoleIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnContentRoleCardsForContentUser() throws Exception {
        mockMvc.perform(get("/api/admin/workbench")
                        .header(SecurityConstants.HEADER_USER_ID, "1")
                        .header(SecurityConstants.HEADER_USERNAME, "editor")
                        .header(SecurityConstants.HEADER_TENANT_ID, "1")
                        .header(SecurityConstants.HEADER_TENANT, "platform")
                        .header(SecurityConstants.HEADER_ROLES, "CONTENT")
                        .header(SecurityConstants.HEADER_AUTHORITIES,
                                "workbench:view,admin:content:update,admin:content:audit,content:item:audit"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.role", is("CONTENT")))
                .andExpect(jsonPath("$.data.todos[?(@.key=='pendingAudit')]").exists())
                .andExpect(jsonPath("$.data.metrics[?(@.key=='contentPending')]").exists())
                .andExpect(jsonPath("$.data.quickEntries[?(@.key=='contentReview')]").exists());
    }

    @Test
    void shouldReturnOperatorRoleCardsWithoutContentAudit() throws Exception {
        mockMvc.perform(get("/api/admin/workbench")
                        .header(SecurityConstants.HEADER_USER_ID, "1")
                        .header(SecurityConstants.HEADER_USERNAME, "operator")
                        .header(SecurityConstants.HEADER_TENANT_ID, "1")
                        .header(SecurityConstants.HEADER_TENANT, "platform")
                        .header(SecurityConstants.HEADER_ROLES, "OPERATOR")
                        .header(SecurityConstants.HEADER_AUTHORITIES,
                                "workbench:view,admin:order:update,admin:member:update,admin:banner:create"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.role", is("OPERATOR")))
                .andExpect(jsonPath("$.data.todos[?(@.key=='pendingOrders')]").exists())
                .andExpect(jsonPath("$.data.todos[?(@.key=='pendingAudit')]").doesNotExist());
    }
}
