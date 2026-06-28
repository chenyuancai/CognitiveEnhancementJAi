package cn.cyc.ai.cog.base;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("base-it")
class BaseFileIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldUploadDownloadAndEnsureViaInnerApi() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "hello.txt",
                "text/plain",
                "hello-base-file".getBytes(StandardCharsets.UTF_8));

        MvcResult upload = mockMvc.perform(multipart("/api/base/files/inner/upload")
                        .file(file)
                        .param("tenantId", "1")
                        .param("bizCode", "cog"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.originalName", is("hello.txt")))
                .andExpect(jsonPath("$.data.status", is("UNCONFIRMED")))
                .andReturn();

        long fileId = objectMapper.readTree(upload.getResponse().getContentAsString())
                .path("data").path("id").asLong();

        mockMvc.perform(get("/api/base/files/" + fileId + "/download"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", org.hamcrest.Matchers.containsString("hello.txt")));

        mockMvc.perform(post("/api/base/files/inner/ensure")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"ids\":[" + fileId + "]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)));

        mockMvc.perform(get("/api/base/files/inner/" + fileId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status", is("CONFIRMED")));
    }

    @Test
    void shouldUploadBytesViaFeignPath() throws Exception {
        String base64 = Base64.getEncoder().encodeToString("bytes-content".getBytes(StandardCharsets.UTF_8));
        mockMvc.perform(post("/api/base/files/inner/upload-bytes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "tenantId": 1,
                                  "bizCode": "cog",
                                  "fileName": "a.bin",
                                  "contentType": "application/octet-stream",
                                  "base64Content": "%s"
                                }
                                """.formatted(base64)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.originalName", is("a.bin")));
    }

    @Test
    void shouldPageUploadedFiles() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "page.txt", "text/plain", "p".getBytes(StandardCharsets.UTF_8));
        mockMvc.perform(multipart("/api/base/files/upload")
                        .file(file)
                        .param("tenantId", "1"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/base/files/page")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"current\":1,\"size\":10,\"tenantId\":1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.records").isArray())
                .andExpect(jsonPath("$.data.total").exists());
    }
}
