package cn.cyc.ai.cog.base;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("base-it")
class BaseDictIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private long saveDictTypeAndGetId(String code, String name) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/base/dict/types/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"code":"%s","name":"%s"}
                                """.formatted(code, name)))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        return root.path("data").path("id").asLong();
    }

    private long saveEnumTypeAndGetId(String code, String name) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/base/enum/types/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"code":"%s","name":"%s"}
                                """.formatted(code, name)))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        return root.path("data").path("id").asLong();
    }

    @Test
    void shouldSaveAndPageDictType() throws Exception {
        mockMvc.perform(post("/api/base/dict/types/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"code":"content_type","name":"内容类型","enName":"ContentType"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.code", is("content_type")));

        mockMvc.perform(post("/api/base/dict/types/page")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.records").isArray());
    }

    @Test
    void shouldSaveDictItemAndListTree() throws Exception {
        mockMvc.perform(post("/api/base/dict/types/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"code":"tree_demo","name":"树形演示"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id", greaterThan(0)));

        String pageBody = mockMvc.perform(post("/api/base/dict/types/page")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"keyword\":\"tree_demo\"}"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        long typeId = Long.parseLong(pageBody.replaceAll("(?s).*\"id\":(\\d+).*", "$1"));

        mockMvc.perform(post("/api/base/dict/items/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"typeId":%d,"parentId":0,"value":"ROOT","label":"根节点","sort":1}
                                """.formatted(typeId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)));

        mockMvc.perform(post("/api/base/dict/items/list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"typeId":%d,"treeFlag":true}
                                """.formatted(typeId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void shouldRejectNonIntegerEnumValue() throws Exception {
        mockMvc.perform(post("/api/base/enum/types/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"code":"status_enum","name":"状态枚举"}
                                """))
                .andExpect(status().isOk());

        String pageBody = mockMvc.perform(post("/api/base/enum/types/page")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"keyword\":\"status_enum\"}"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        long typeId = Long.parseLong(pageBody.replaceAll("(?s).*\"id\":(\\d+).*", "$1"));

        mockMvc.perform(post("/api/base/enum/items/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"typeId":%d,"value":"ABC","label":"非法枚举","sort":1}
                                """.formatted(typeId)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnEnabledItemsByCode() throws Exception {
        mockMvc.perform(post("/api/base/dict/types/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"code":"banner_position","name":"Banner位"}
                                """))
                .andExpect(status().isOk());

        String pageBody = mockMvc.perform(post("/api/base/dict/types/page")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"keyword\":\"banner_position\"}"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        long typeId = Long.parseLong(pageBody.replaceAll("(?s).*\"id\":(\\d+).*", "$1"));

        mockMvc.perform(post("/api/base/dict/items/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"typeId":%d,"value":"HOME_TOP","label":"首页顶部","sort":1}
                                """.formatted(typeId)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/base/dict/banner_position/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data[0].value", is("HOME_TOP")));
    }
}
