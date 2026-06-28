package cn.cyc.ai.cog.center.model.provider;

import cn.cyc.ai.cog.center.model.catalog.InMemoryModelCatalogRepository;
import cn.cyc.ai.cog.core.metadata.type.CommonStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ModelProviderControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        InMemoryModelCatalogRepository catalogRepository = new InMemoryModelCatalogRepository();
        CatalogModelProviderRepository providerRepository = new CatalogModelProviderRepository(catalogRepository);
        ModelProviderAdminService service = new ModelProviderAdminService(providerRepository, catalogRepository);
        ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
        mockMvc = MockMvcBuilders.standaloneSetup(new ModelProviderAdminController(service))
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }

    @Test
    void shouldSupportProviderCrudFlow() throws Exception {
        String createPayload = """
                {
                  "providerCode": "openai",
                  "providerName": "OpenAI",
                  "providerType": "OPENAI_COMPATIBLE",
                  "defaultEndpoint": "https://api.openai.com/v1/chat/completions",
                  "apiKey": "sk-openai-test",
                  "status": "ENABLED"
                }
                """;

        mockMvc.perform(post("/api/center/model-providers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.providerCode", is("openai")))
                .andExpect(jsonPath("$.data.apiKeyConfigured", is(true)));

        mockMvc.perform(get("/api/center/model-providers/openai"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.providerName", is("OpenAI")))
                .andExpect(jsonPath("$.data.apiKeyMask", is("****test")));

        mockMvc.perform(get("/api/center/model-providers/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].providerCode", is("openai")));

        mockMvc.perform(post("/api/center/model-providers/page")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"page\":1,\"size\":10}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.total", is(1)));
    }
}
