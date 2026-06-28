package cn.cyc.ai.cog.admin;

import cn.cyc.ai.cog.infra.web.TraceContextFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Admin 健康检查集成测试（H2 测试 profile 下无需 admin 业务表）。
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"admin-it"})
class AdminSystemHealthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnHealthReportWithServices() throws Exception {
        mockMvc.perform(get("/api/admin/system/health"))
                .andExpect(status().isOk())
                .andExpect(header().exists(TraceContextFilter.TRACE_ID_HEADER))
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.status", notNullValue()))
                .andExpect(jsonPath("$.data.checkedAt", notNullValue()))
                .andExpect(jsonPath("$.data.services").isArray())
                .andExpect(jsonPath("$.data.services[?(@.key=='database')]").exists());
    }
}
