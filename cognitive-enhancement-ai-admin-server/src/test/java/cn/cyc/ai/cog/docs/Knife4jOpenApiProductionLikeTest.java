package cn.cyc.ai.cog.docs;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 接近生产配置的 OpenAPI 文档生成校验。
 */
@SpringBootTest(properties = {
        "cog.persistence.enabled=true",
        "spring.flyway.enabled=false",
        "spring.datasource.url=jdbc:h2:mem:swagger-prodlike;MODE=MySQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.sql.init.mode=always",
        "spring.sql.init.schema-locations=classpath:db/center-it-schema.sql",
        "cog.jwt.auth-enabled=true",
        "cog.seed.enabled=false"
})
@AutoConfigureMockMvc
class Knife4jOpenApiProductionLikeTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldGenerateOpenApiDocsWithPersistenceEnabled() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("openapi")))
                .andExpect(content().string(not(containsString("\"code\":\"C0500\""))));
    }
}
