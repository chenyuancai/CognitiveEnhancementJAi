package cn.cyc.ai.cog.center.model;

import cn.cyc.ai.cog.center.model.catalog.InMemoryModelCatalogRepository;
import cn.cyc.ai.cog.center.model.catalog.ModelCatalogRepository;
import cn.cyc.ai.cog.center.model.provider.CatalogModelProviderRepository;
import cn.cyc.ai.cog.center.model.provider.ModelProviderAdminController;
import cn.cyc.ai.cog.center.model.provider.ModelProviderAdminService;
import cn.cyc.ai.cog.center.model.provider.ModelProviderUpsertRequest;
import cn.cyc.ai.cog.core.metadata.model.ModelDefinition;
import cn.cyc.ai.cog.core.metadata.type.CommonStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ModelControllerTest {

    private MockMvc mockMvc;

    private ModelCatalogRepository catalogRepository;

    @BeforeEach
    void setUp() {
        catalogRepository = new InMemoryModelCatalogRepository();
        CatalogModelProviderRepository providerRepository = new CatalogModelProviderRepository(catalogRepository);
        ModelProviderAdminService providerAdminService = new ModelProviderAdminService(providerRepository, catalogRepository);
        ModelAdminService service = new ModelAdminService(catalogRepository);
        CatalogModelDefinitionRepository routeRepository = new CatalogModelDefinitionRepository(catalogRepository);
        ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
        mockMvc = MockMvcBuilders.standaloneSetup(new ModelAdminController(service), new ModelProviderAdminController(providerAdminService))
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();

        providerAdminService.seed(new ModelProviderUpsertRequest(
                "openai", "OpenAI", "OPENAI_COMPATIBLE",
                "https://api.openai.com/v1/chat/completions",
                "sk-openai-test", null, CommonStatus.ENABLED));
        routeRepository.save(new ModelDefinition(
                "openai",
                "OpenAI",
                "gpt-4o-mini",
                "GPT-4o Mini",
                "CHAT",
                "https://api.openai.com/v1/chat/completions",
                "sk-openai-test",
                30000,
                2,
                CommonStatus.ENABLED,
                10,
                null
        ));
    }

    @Test
    void shouldPageAndFilterModels() throws Exception {
        routeRepository().save(new ModelDefinition(
                "dashscope",
                "DashScope",
                "qwen-plus",
                "Qwen Plus",
                "CHAT",
                "https://dashscope.aliyuncs.com/compatible-mode/v1",
                "sk-dashscope-test",
                30000,
                1,
                CommonStatus.DISABLED,
                5,
                null
        ));
        providerAdminSeedDashscope();

        mockMvc.perform(post("/api/center/models/page")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "page": 1,
                                  "size": 1,
                                  "keyword": "gpt",
                                  "status": "ENABLED",
                                  "providerCode": "openai",
                                  "sort": "code,asc"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.total", is(1)))
                .andExpect(jsonPath("$.data.items[0].modelCode", is("gpt-4o-mini")));
    }

    @Test
    void shouldSupportModelCrudWithManyProviders() throws Exception {
        mockMvc.perform(post("/api/center/models/page")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.items[0].modelCode", is("gpt-4o-mini")));

        String createPayload = """
                {
                  "modelCode": "gpt-4.1",
                  "modelName": "GPT-4.1",
                  "modelType": "CHAT",
                  "timeoutMs": 30000,
                  "retryTimes": 2,
                  "status": "ENABLED",
                  "fallbackModelCode": "gpt-4o-mini",
                  "providerBindings": [
                    {
                      "providerCode": "openai",
                      "routePriority": 20,
                      "status": "ENABLED"
                    }
                  ]
                }
                """;

        mockMvc.perform(post("/api/center/models")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.modelCode", is("gpt-4.1")))
                .andExpect(jsonPath("$.data.providers[0].providerCode", is("openai")));

        mockMvc.perform(get("/api/center/models/gpt-4.1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.providers", org.hamcrest.Matchers.hasSize(1)));
    }

    private CatalogModelDefinitionRepository routeRepository() {
        return new CatalogModelDefinitionRepository(catalogRepository);
    }

    private void providerAdminSeedDashscope() {
        CatalogModelProviderRepository providerRepository = new CatalogModelProviderRepository(catalogRepository);
        ModelProviderAdminService providerAdminService = new ModelProviderAdminService(providerRepository, catalogRepository);
        providerAdminService.seed(new ModelProviderUpsertRequest(
                "dashscope", "DashScope", "DASHSCOPE",
                "https://dashscope.aliyuncs.com/compatible-mode/v1",
                "sk-dashscope-test", null, CommonStatus.ENABLED));
    }
}
