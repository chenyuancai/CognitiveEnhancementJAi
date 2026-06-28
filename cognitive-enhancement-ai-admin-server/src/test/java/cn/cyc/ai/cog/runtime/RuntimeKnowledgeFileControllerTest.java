package cn.cyc.ai.cog.runtime;

import cn.cyc.ai.cog.infra.web.TraceContextFilter;
import cn.cyc.ai.cog.runtime.support.RuntimeLlmTestDoubleConfiguration;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Runtime 知识库与文件处理集成测试。
 *
 * @author cyc
 */
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Import(RuntimeLlmTestDoubleConfiguration.class)
class RuntimeKnowledgeFileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @TempDir
    Path tempDir;

    @Test
    void shouldManageKnowledgeFragmentsBindingsAndRetrieve() throws Exception {
        mockMvc.perform(post("/api/runtime/knowledge/fragments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "knowledgeCode": "knowledge.qa",
                                  "title": "退款政策",
                                  "content": "用户可在 7 天内申请无理由退款。",
                                  "tags": ["policy", "refund"]
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.knowledgeCode", is("knowledge.qa")));

        mockMvc.perform(post("/api/runtime/knowledge/bindings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "scenarioCode": "qa",
                                  "knowledgeCode": "knowledge.qa",
                                  "priority": 10,
                                  "enabled": true
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)));

        mockMvc.perform(get("/api/runtime/knowledge/retrieve")
                        .param("scenarioCode", "qa")
                        .param("query", "退款"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.total", is(1)))
                .andExpect(jsonPath("$.data.items[0].title", is("退款政策")));
    }

    @Test
    void shouldRegisterParseFileAndInjectIntoCapabilityExecution() throws Exception {
        Path policyFile = tempDir.resolve("policy.txt");
        Files.writeString(policyFile, "支持 7 天无理由退款", StandardCharsets.UTF_8);

        String createResponse = mockMvc.perform(post("/api/runtime/files")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "fileName": "policy.txt",
                                  "contentType": "text/plain",
                                  "sizeBytes": %d,
                                  "storagePath": "%s",
                                  "checksum": "abc123"
                                }
                                """.formatted(Files.size(policyFile), policyFile.toString())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andReturn()
                .getResponse()
                .getContentAsString();
        String fileId = JsonPath.read(createResponse, "$.data.fileId");

        mockMvc.perform(post("/api/runtime/files/parse")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fileId\": \"" + fileId + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status", is("SUCCEEDED")));

        mockMvc.perform(get("/api/runtime/files/{fileId}/parse-result", fileId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.parseResult").value(org.hamcrest.Matchers.containsString("支持 7 天无理由退款")));

        mockMvc.perform(post("/api/runtime/knowledge/fragments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "knowledgeCode": "knowledge.qa",
                                  "title": "FAQ",
                                  "content": "支持 7 天无理由退款",
                                  "tags": []
                                }
                                """))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/runtime/knowledge/bindings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "scenarioCode": "qa",
                                  "knowledgeCode": "knowledge.qa",
                                  "priority": 1,
                                  "enabled": true
                                }
                                """))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/runtime/capabilities/execute")
                        .header(TraceContextFilter.TRACE_ID_HEADER, "trace-knowledge-file-001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "capabilityCode": "capability.qa.answer",
                                  "input": {
                                    "question": "退款政策是什么?"
                                  },
                                  "parameters": {
                                    "knowledgeEnabled": true,
                                    "fileId": "%s"
                                  }
                                }
                """.formatted(fileId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.result.status", is("SUCCESS")))
                .andExpect(jsonPath("$.data.result.output.executorType", is("REACT")))
                .andExpect(jsonPath("$.data.result.output.businessOutput.answer", is("这是百炼返回的演示回答。")));
    }
}
