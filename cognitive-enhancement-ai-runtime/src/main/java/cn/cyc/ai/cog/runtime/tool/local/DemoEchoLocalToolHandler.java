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
 * 演示用 Echo Tool 本地处理器。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Component
public class DemoEchoLocalToolHandler implements LocalToolHandler {

    /** 日志记录器 */
    private static final Logger log = LoggerFactory.getLogger(DemoEchoLocalToolHandler.class);

    /**
     * 执行implRef。
     * @return 执行结果
     */
    @Override
    public String implRef() {
        return "demoEchoTool";
    }

    /**
     * 执行操作。
     *
     * @param context 上下文
     * @param request 请求
     * @return 执行结果
     */
    @Override
    public Object invoke(ExecutionContext context, ToolInvocationRequest request) {
        log.info("执行本地 DemoEchoTool, traceId={}, capabilityCode={}",
                context.traceId(), context.capability().capabilityCode());

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("handler", implRef());
        result.put("answer", "这是 Echo Tool 返回的演示结果。");
        result.put("answerPreview", "这是 Echo Tool 返回的演示结果。");
        result.put("input", request.input());
        result.put("parameters", request.parameters());
        return result;
    }
}
