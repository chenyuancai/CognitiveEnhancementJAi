package cn.cyc.ai.cog.runtime.tool.adapter;

import cn.cyc.ai.cog.core.exception.BusinessException;
import cn.cyc.ai.cog.core.metadata.tool.ToolDefinition;
import cn.cyc.ai.cog.core.metadata.tool.ToolProtocolType;
import cn.cyc.ai.cog.runtime.tool.adapter.spi.ToolAdapter;
import cn.cyc.ai.cog.runtime.tool.adapter.spi.ToolAdapterContext;
import cn.cyc.ai.cog.runtime.tool.local.LocalToolRegistry;
import org.springframework.stereotype.Component;

/**
 * Java 本地 Tool Adapter。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Component
public class JavaLocalToolAdapter implements ToolAdapter {

    /** local工具Registry。 */
    private final LocalToolRegistry localToolRegistry;

    /**
     * 创建JavaLocalToolAdapter。
     *
     * @param localToolRegistry local工具Registry
     */
    public JavaLocalToolAdapter(LocalToolRegistry localToolRegistry) {
        this.localToolRegistry = localToolRegistry;
    }

    /**
     * 执行protocol类型。
     * @return 执行结果
     */
    @Override
    public ToolProtocolType protocolType() {
        return ToolProtocolType.JAVA_LOCAL;
    }

    /**
     * 执行操作。
     *
     * @param context 上下文
     * @return 执行结果
     */
    @Override
    public Object invoke(ToolAdapterContext context) {
        ToolDefinition tool = context.tool();
        return localToolRegistry.find(tool.implRef())
                .map(handler -> handler.invoke(context.executionContext(), context.request()))
                .orElseThrow(() -> new BusinessException(
                        "CONFLICT",
                        "未找到 Java 本地 Tool 处理器: " + tool.implRef()
                ));
    }
}
