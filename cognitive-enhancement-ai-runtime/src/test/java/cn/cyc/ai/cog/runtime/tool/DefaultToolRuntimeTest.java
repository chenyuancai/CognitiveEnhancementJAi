package cn.cyc.ai.cog.runtime.tool;

import cn.cyc.ai.cog.core.metadata.agent.AgentDefinition;
import cn.cyc.ai.cog.core.metadata.capability.CapabilityDefinition;
import cn.cyc.ai.cog.core.metadata.skill.SkillDefinition;
import cn.cyc.ai.cog.core.metadata.tool.ToolDefinition;
import cn.cyc.ai.cog.core.metadata.tool.ToolDefinitionRepository;
import cn.cyc.ai.cog.core.metadata.tool.ToolProtocolType;
import cn.cyc.ai.cog.core.metadata.tool.RetryPolicy;
import cn.cyc.ai.cog.core.exception.BusinessException;
import cn.cyc.ai.cog.core.metadata.type.CommonStatus;
import cn.cyc.ai.cog.core.metadata.type.ExecutionMode;
import cn.cyc.ai.cog.core.metadata.type.RiskLevel;
import cn.cyc.ai.cog.core.metadata.type.SchemaDefinition;
import cn.cyc.ai.cog.core.runtime.CapabilityExecuteRequest;
import cn.cyc.ai.cog.core.runtime.ExecutionContext;
import cn.cyc.ai.cog.runtime.api.ToolHttpRequest;
import cn.cyc.ai.cog.runtime.api.ToolHttpResponse;
import cn.cyc.ai.cog.runtime.api.ToolInvocationResult;
import cn.cyc.ai.cog.runtime.tool.adapter.HttpToolAdapter;
import cn.cyc.ai.cog.runtime.tool.adapter.JavaLocalToolAdapter;
import cn.cyc.ai.cog.runtime.tool.adapter.McpToolAdapter;
import cn.cyc.ai.cog.runtime.tool.adapter.ToolAdapterRegistry;
import cn.cyc.ai.cog.runtime.tool.mcp.LocalMcpToolClient;
import cn.cyc.ai.cog.runtime.tool.mcp.RoutingMcpToolClient;
import cn.cyc.ai.cog.runtime.tool.spi.ToolHttpExecutor;
import cn.cyc.ai.cog.runtime.tool.local.LocalToolRegistry;
import cn.cyc.ai.cog.runtime.tool.validation.DefaultToolInputSchemaValidator;
import cn.cyc.ai.cog.runtime.trace.repository.InMemoryTraceSpanRepository;
import cn.cyc.ai.cog.runtime.trace.span.TraceSpanRecorder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DefaultToolRuntimeTest {

    @Test
    void shouldReturnRealPayloadForHttpTool() {
        ToolDefinitionRepository repository = mock(ToolDefinitionRepository.class);
        ToolHttpExecutor toolHttpExecutor = mock(ToolHttpExecutor.class);
        DefaultToolInputSchemaValidator validator = new DefaultToolInputSchemaValidator();
        ObjectMapper objectMapper = new ObjectMapper();

        SchemaDefinition requestSchema = new SchemaDefinition(
                "object", "request", true,
                Map.of("question", new SchemaDefinition("string", "question", true, Map.of(), null, List.of())),
                null, List.of()
        );
        SchemaDefinition responseSchema = new SchemaDefinition("object", "response", true, Map.of(), null, List.of());
        ToolDefinition tool = new ToolDefinition(
                "tool.http.echo", "HTTP Echo", ToolProtocolType.HTTP,
                requestSchema, responseSchema, "http:echo", RiskLevel.LOW, 5000, new RetryPolicy(1),
                "http://127.0.0.1:8089/echo", CommonStatus.ENABLED
        );
        when(repository.findByCode("tool.http.echo")).thenReturn(Optional.of(tool));
        when(toolHttpExecutor.execute(any(ToolHttpRequest.class))).thenReturn(
                new ToolHttpResponse(200, "{\"answer\":\"remote\"}", 12)
        );

        DefaultToolRuntime runtime = runtime(
                repository, new LocalToolRegistry(List.of()), toolHttpExecutor, validator,
                new RoutingMcpToolClient(toolHttpExecutor, objectMapper),
                objectMapper
        );
        ExecutionContext context = executionContext();

        ToolInvocationResult result = runtime.invoke(context, "tool.http.echo", Map.of("question", "hello"));

        assertFalse(result.mock());
        assertEquals("HTTP", result.protocolType());
        assertEquals("remote", ((Map<?, ?>) result.toolPayload()).get("answer"));
    }

    @Test
    void shouldReturnLocalMcpPayloadForMcpTool() {
        ToolDefinitionRepository repository = mock(ToolDefinitionRepository.class);
        DefaultToolInputSchemaValidator validator = new DefaultToolInputSchemaValidator();
        ObjectMapper objectMapper = new ObjectMapper();

        SchemaDefinition requestSchema = new SchemaDefinition(
                "object", "request", true,
                Map.of("question", new SchemaDefinition("string", "question", true, Map.of(), null, List.of())),
                null, List.of()
        );
        SchemaDefinition responseSchema = new SchemaDefinition("object", "response", true, Map.of(), null, List.of());
        ToolDefinition tool = new ToolDefinition(
                "tool.mcp.echo", "MCP Echo", ToolProtocolType.MCP,
                requestSchema, responseSchema, "mcp:echo", RiskLevel.LOW, 5000, new RetryPolicy(1),
                "{\"server\":\"local\",\"tool\":\"demoEcho\"}", CommonStatus.ENABLED
        );
        when(repository.findByCode("tool.mcp.echo")).thenReturn(Optional.of(tool));

        DefaultToolRuntime runtime = runtime(
                repository, new LocalToolRegistry(List.of()), mock(ToolHttpExecutor.class), validator,
                new RoutingMcpToolClient(mock(ToolHttpExecutor.class), objectMapper),
                objectMapper
        );

        ToolInvocationResult result = runtime.invoke(
                executionContext(List.of(skill("skill.mcp", "tool.mcp.echo"))),
                "tool.mcp.echo",
                Map.of("question", "mcp")
        );

        assertTrue(result.mock());
        assertEquals("MCP", result.protocolType());
        assertEquals("demoEcho", ((Map<?, ?>) result.toolPayload()).get("tool"));
    }

    @Test
    void shouldRejectToolWhenNoLoadedSkillBindsIt() {
        ToolDefinitionRepository repository = mock(ToolDefinitionRepository.class);
        ToolDefinition tool = sampleHttpTool(new RetryPolicy(1));
        when(repository.findByCode("tool.http.echo")).thenReturn(Optional.of(tool));

        ObjectMapper objectMapper = new ObjectMapper();
        ToolHttpExecutor toolHttpExecutor = mock(ToolHttpExecutor.class);
        DefaultToolRuntime runtime = runtime(
                repository, new LocalToolRegistry(List.of()), toolHttpExecutor,
                new DefaultToolInputSchemaValidator(),
                new RoutingMcpToolClient(toolHttpExecutor, objectMapper),
                objectMapper
        );
        ExecutionContext context = executionContext(List.of(skill("skill.other", "tool.other")));

        BusinessException exception = assertThrows(BusinessException.class,
                () -> runtime.invoke(context, "tool.http.echo", Map.of("question", "hello")));

        assertEquals("FORBIDDEN", exception.getSemanticCode());
    }

    @Test
    void shouldRetryHttpToolUntilSuccess() {
        ToolDefinitionRepository repository = mock(ToolDefinitionRepository.class);
        ToolHttpExecutor toolHttpExecutor = mock(ToolHttpExecutor.class);
        ToolDefinition tool = sampleHttpTool(new RetryPolicy(3));
        when(repository.findByCode("tool.http.echo")).thenReturn(Optional.of(tool));
        when(toolHttpExecutor.execute(any(ToolHttpRequest.class)))
                .thenReturn(new ToolHttpResponse(502, "{\"error\":\"bad gateway\"}", 10))
                .thenReturn(new ToolHttpResponse(200, "{\"answer\":\"remote\"}", 12));

        ObjectMapper objectMapper = new ObjectMapper();
        DefaultToolRuntime runtime = runtime(
                repository, new LocalToolRegistry(List.of()), toolHttpExecutor,
                new DefaultToolInputSchemaValidator(),
                new RoutingMcpToolClient(toolHttpExecutor, objectMapper),
                objectMapper
        );

        ToolInvocationResult result = runtime.invoke(
                executionContext(List.of(skill("skill.qa", "tool.http.echo"))),
                "tool.http.echo",
                Map.of("question", "hello")
        );

        assertEquals("remote", ((Map<?, ?>) result.toolPayload()).get("answer"));
        verify(toolHttpExecutor, times(2)).execute(any(ToolHttpRequest.class));
    }

    @Test
    void shouldPassToolTimeoutToHttpExecutor() {
        ToolDefinitionRepository repository = mock(ToolDefinitionRepository.class);
        ToolHttpExecutor toolHttpExecutor = mock(ToolHttpExecutor.class);
        ToolDefinition tool = sampleHttpTool(new RetryPolicy(1));
        when(repository.findByCode("tool.http.echo")).thenReturn(Optional.of(tool));
        when(toolHttpExecutor.execute(any(ToolHttpRequest.class))).thenReturn(
                new ToolHttpResponse(200, "{\"answer\":\"remote\"}", 12)
        );
        ObjectMapper objectMapper = new ObjectMapper();
        DefaultToolRuntime runtime = runtime(
                repository, new LocalToolRegistry(List.of()), toolHttpExecutor,
                new DefaultToolInputSchemaValidator(),
                new RoutingMcpToolClient(toolHttpExecutor, objectMapper),
                objectMapper
        );

        runtime.invoke(
                executionContext(List.of(skill("skill.qa", "tool.http.echo"))),
                "tool.http.echo",
                Map.of("question", "hello")
        );

        ArgumentCaptor<ToolHttpRequest> requestCaptor = forClass(ToolHttpRequest.class);
        verify(toolHttpExecutor).execute(requestCaptor.capture());
        assertEquals(Duration.ofMillis(5000), requestCaptor.getValue().timeout());
    }

    @Test
    void shouldExposeToolRiskLevelInInvocationResult() {
        ToolDefinitionRepository repository = mock(ToolDefinitionRepository.class);
        ToolHttpExecutor toolHttpExecutor = mock(ToolHttpExecutor.class);
        ToolDefinition tool = sampleHttpTool(new RetryPolicy(1), RiskLevel.HIGH);
        when(repository.findByCode("tool.http.echo")).thenReturn(Optional.of(tool));
        when(toolHttpExecutor.execute(any(ToolHttpRequest.class))).thenReturn(
                new ToolHttpResponse(200, "{\"answer\":\"remote\"}", 12)
        );
        ObjectMapper objectMapper = new ObjectMapper();
        DefaultToolRuntime runtime = runtime(
                repository, new LocalToolRegistry(List.of()), toolHttpExecutor,
                new DefaultToolInputSchemaValidator(),
                new RoutingMcpToolClient(toolHttpExecutor, objectMapper),
                objectMapper
        );

        ToolInvocationResult result = runtime.invoke(
                executionContext(List.of(skill("skill.qa", "tool.http.echo"))),
                "tool.http.echo",
                Map.of("question", "hello")
        );

        assertEquals("HIGH", result.riskLevel());
    }

    @Test
    void shouldRejectJavaLocalToolWhenHandlerMissing() {
        ToolDefinitionRepository repository = mock(ToolDefinitionRepository.class);
        ToolDefinition tool = sampleJavaLocalTool();
        when(repository.findByCode("tool.local.missing")).thenReturn(Optional.of(tool));

        ObjectMapper objectMapper = new ObjectMapper();
        ToolHttpExecutor toolHttpExecutor = mock(ToolHttpExecutor.class);
        DefaultToolRuntime runtime = runtime(
                repository, new LocalToolRegistry(List.of()), toolHttpExecutor,
                new DefaultToolInputSchemaValidator(),
                new RoutingMcpToolClient(toolHttpExecutor, objectMapper),
                objectMapper
        );

        BusinessException exception = assertThrows(BusinessException.class,
                () -> runtime.invoke(
                        executionContext(List.of(skill("skill.qa", "tool.local.missing"))),
                        "tool.local.missing",
                        Map.of("question", "hello")
                ));

        assertEquals("CONFLICT", exception.getSemanticCode());
    }

    private DefaultToolRuntime runtime(ToolDefinitionRepository repository,
                                       LocalToolRegistry localToolRegistry,
                                       ToolHttpExecutor toolHttpExecutor,
                                       DefaultToolInputSchemaValidator validator,
                                       RoutingMcpToolClient mcpToolClient,
                                       ObjectMapper objectMapper) {
        ToolAdapterRegistry registry = new ToolAdapterRegistry(List.of(
                new JavaLocalToolAdapter(localToolRegistry),
                new HttpToolAdapter(toolHttpExecutor, objectMapper),
                new McpToolAdapter(mcpToolClient)
        ));
        return new DefaultToolRuntime(
                repository, validator, registry,
                new TraceSpanRecorder(new InMemoryTraceSpanRepository(), List.of())
        );
    }

    private ExecutionContext executionContext() {
        return executionContext(List.of(skill("skill.qa", "tool.http.echo")));
    }

    private ExecutionContext executionContext(List<SkillDefinition> skills) {
        SchemaDefinition schema = new SchemaDefinition("object", "input", true, Map.of(), null, List.of());
        CapabilityDefinition capability = new CapabilityDefinition(
                "capability.qa.answer", "问答", "desc", schema, schema, Map.of(),
                ExecutionMode.SYNC, "agent.qa", RiskLevel.LOW, false, CommonStatus.ENABLED
        );
        AgentDefinition agent = new AgentDefinition(
                "agent.qa", "问答 Agent", "role", "goal", "gpt-4o-mini",
                4, BigDecimal.ONE, 20000, List.of("skill.qa"), Map.of(), CommonStatus.ENABLED
        );
        CapabilityExecuteRequest request = new CapabilityExecuteRequest(
                "capability.qa.answer", Map.of("question", "hello"), Map.of()
        );
        return new ExecutionContext("trace-tool-http", request, capability, agent, null, skills, Map.of());
    }

    private SkillDefinition skill(String skillCode, String toolCode) {
        return new SkillDefinition(
                skillCode, "技能", "TOOL", "instruction", List.of(toolCode),
                RiskLevel.LOW, List.of(), List.of(), List.of(), CommonStatus.ENABLED
        );
    }

    private ToolDefinition sampleHttpTool(RetryPolicy retryPolicy) {
        return sampleHttpTool(retryPolicy, RiskLevel.LOW);
    }

    private ToolDefinition sampleHttpTool(RetryPolicy retryPolicy, RiskLevel riskLevel) {
        SchemaDefinition requestSchema = new SchemaDefinition(
                "object", "request", true,
                Map.of("question", new SchemaDefinition("string", "question", true, Map.of(), null, List.of())),
                null, List.of()
        );
        SchemaDefinition responseSchema = new SchemaDefinition("object", "response", true, Map.of(), null, List.of());
        return new ToolDefinition(
                "tool.http.echo", "HTTP Echo", ToolProtocolType.HTTP,
                requestSchema, responseSchema, "http:echo", riskLevel, 5000, retryPolicy,
                "http://127.0.0.1:8089/echo", CommonStatus.ENABLED
        );
    }

    private ToolDefinition sampleJavaLocalTool() {
        SchemaDefinition requestSchema = new SchemaDefinition(
                "object", "request", true,
                Map.of("question", new SchemaDefinition("string", "question", true, Map.of(), null, List.of())),
                null, List.of()
        );
        SchemaDefinition responseSchema = new SchemaDefinition("object", "response", true, Map.of(), null, List.of());
        return new ToolDefinition(
                "tool.local.missing", "Missing Local Tool", ToolProtocolType.JAVA_LOCAL,
                requestSchema, responseSchema, "local:missing", RiskLevel.LOW, 5000,
                new RetryPolicy(1), "missingLocalHandler", CommonStatus.ENABLED
        );
    }
}
