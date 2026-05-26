package cn.cyc.ai.cog.center.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import cn.cyc.ai.cog.core.metadata.model.ModelDefinition;
import cn.cyc.ai.cog.core.metadata.type.CommonStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ModelControllerTest {

    private MockMvc mockMvc;

    private InMemoryModelDefinitionRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryModelDefinitionRepository();
        ModelAdminService service = new ModelAdminService(repository);
        ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
        mockMvc = MockMvcBuilders.standaloneSetup(new ModelAdminController(service))
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();

        repository.save(new ModelDefinition(
                "openai",
                "OpenAI",
                "gpt-4o-mini",
                "GPT-4o Mini",
                "CHAT",
                "https://api.openai.com/v1/chat/completions",
                "credential/openai/default",
                30000,
                2,
                CommonStatus.ENABLED,
                10,
                null
        ));
    }

    @Test
    void shouldSupportModelCrudMainFlow() throws Exception {
        mockMvc.perform(get("/api/center/models"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.items[0].modelCode", is("gpt-4o-mini")))
                .andExpect(jsonPath("$.data.items[*].modelCode", hasItem("gpt-4o-mini")));

        String createPayload = """
                {
                  "providerCode": "openai",
                  "providerName": "OpenAI",
                  "modelCode": "gpt-4.1",
                  "modelName": "GPT-4.1",
                  "modelType": "CHAT",
                  "endpoint": "https://api.openai.com/v1/chat/completions",
                  "credentialRef": "credential/openai/default",
                  "timeoutMs": 30000,
                  "retryTimes": 2,
                  "status": "ENABLED",
                  "routePriority": 20,
                  "fallbackModelCode": "gpt-4o-mini"
                }
                """;

        mockMvc.perform(post("/api/center/models")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.modelCode", is("gpt-4.1")))
                .andExpect(jsonPath("$.data.modelName", is("GPT-4.1")));

        mockMvc.perform(get("/api/center/models/gpt-4.1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.modelCode", is("gpt-4.1")))
                .andExpect(jsonPath("$.data.routePriority", is(20)));

        String updatePayload = """
                {
                  "providerCode": "openai",
                  "providerName": "OpenAI",
                  "modelName": "GPT-4.1 Turbo",
                  "modelType": "CHAT",
                  "endpoint": "https://api.openai.com/v1/chat/completions",
                  "credentialRef": "credential/openai/default",
                  "timeoutMs": 45000,
                  "retryTimes": 3,
                  "status": "ENABLED",
                  "routePriority": 10,
                  "fallbackModelCode": "gpt-4o-mini"
                }
                """;

        mockMvc.perform(put("/api/center/models/gpt-4.1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatePayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.modelName", is("GPT-4.1 Turbo")))
                .andExpect(jsonPath("$.data.timeoutMs", is(45000)));

        mockMvc.perform(get("/api/center/models"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.total", is(2)))
                .andExpect(jsonPath("$.data.items[*].modelCode", hasItem("gpt-4.1")));
    }
}
