package cn.cyc.ai.cog.runtime.tool.adapter;

import cn.cyc.ai.cog.core.metadata.tool.ToolDefinition;
import cn.cyc.ai.cog.core.metadata.tool.ToolProtocolType;
import cn.cyc.ai.cog.runtime.tool.adapter.spi.ToolAdapter;
import cn.cyc.ai.cog.runtime.tool.adapter.spi.ToolAdapterContext;
import cn.cyc.ai.cog.runtime.tool.local.LocalToolRegistry;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Java 本地 Tool Adapter。
 *
 * @author cyc
 */
@Component
public class JavaLocalToolAdapter implements ToolAdapter {

    private final LocalToolRegistry localToolRegistry;

    public JavaLocalToolAdapter(LocalToolRegistry localToolRegistry) {
        this.localToolRegistry = localToolRegistry;
    }

    @Override
    public ToolProtocolType protocolType() {
        return ToolProtocolType.JAVA_LOCAL;
    }

    @Override
    public Object invoke(ToolAdapterContext context) {
        ToolDefinition tool = context.tool();
        return localToolRegistry.find(tool.implRef())
                .map(handler -> handler.invoke(context.executionContext(), context.request()))
                .orElseGet(() -> fallbackPayload(tool));
    }

    private Map<String, Object> fallbackPayload(ToolDefinition tool) {
        Map<String, Object> fallback = new LinkedHashMap<>();
        fallback.put("handler", "mock-fallback");
        fallback.put("implRef", tool.implRef());
        return fallback;
    }
}
