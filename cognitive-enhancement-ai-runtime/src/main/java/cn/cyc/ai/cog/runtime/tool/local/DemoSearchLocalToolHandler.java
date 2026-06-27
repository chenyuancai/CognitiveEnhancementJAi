package cn.cyc.ai.cog.runtime.tool.local;

import cn.cyc.ai.cog.runtime.api.ToolInvocationRequest;
import cn.cyc.ai.cog.core.runtime.ExecutionContext;
import cn.cyc.ai.cog.runtime.spi.LocalToolHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 演示用搜索 Tool 本地处理器。
 *
 * @author cyc
 */
@Component
public class DemoSearchLocalToolHandler implements LocalToolHandler {

    /**
     * 处理器日志。
     */
    private static final Logger log = LoggerFactory.getLogger(DemoSearchLocalToolHandler.class);

    /**
     * 返回处理器绑定的实现引用。
     *
     * @return 实现引用编码
     */
    @Override
    public String implRef() {
        return "demoSearchTool";
    }

    /**
     * 执行演示搜索逻辑。
     *
     * @param context 运行时上下文
     * @param request Tool 调用请求
     * @return 演示搜索结果
     */
    @Override
    public Object invoke(ExecutionContext context, ToolInvocationRequest request) {
        log.info("执行本地 DemoSearchTool, traceId={}, capabilityCode={}, parameterKeys={}",
                context.traceId(),
                context.capability().capabilityCode(),
                request.parameters().keySet());

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("handler", implRef());
        result.put("answer", "这是本地 Tool 返回的演示检索结果。");
        result.put("answerPreview", "这是本地 Tool 返回的演示检索结果。");
        result.put("input", request.input());
        result.put("parameters", request.parameters());
        return result;
    }
}
