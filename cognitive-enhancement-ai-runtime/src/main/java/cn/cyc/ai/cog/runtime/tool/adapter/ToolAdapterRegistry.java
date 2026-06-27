package cn.cyc.ai.cog.runtime.tool.adapter;

import cn.cyc.ai.cog.core.exception.BusinessException;
import cn.cyc.ai.cog.core.metadata.tool.ToolProtocolType;
import cn.cyc.ai.cog.runtime.tool.adapter.spi.ToolAdapter;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Tool Adapter 注册表，按协议类型路由到对应 Adapter。
 *
 * @author cyc
 */
@Component
public class ToolAdapterRegistry {

    private final Map<ToolProtocolType, ToolAdapter> adapters;

    /**
     * 构造 Tool Adapter 注册表。
     *
     * @param adapterList Spring 注入的全部 Adapter
     */
    public ToolAdapterRegistry(List<ToolAdapter> adapterList) {
        Map<ToolProtocolType, ToolAdapter> mapped = new EnumMap<>(ToolProtocolType.class);
        for (ToolAdapter adapter : adapterList) {
            mapped.put(adapter.protocolType(), adapter);
        }
        this.adapters = Map.copyOf(mapped);
    }

    /**
     * 按协议类型获取 Adapter。
     *
     * @param protocolType 协议类型
     * @return Tool Adapter
     */
    public ToolAdapter get(ToolProtocolType protocolType) {
        ToolAdapter adapter = adapters.get(protocolType);
        if (adapter == null) {
            throw new BusinessException("CONFLICT", "未注册 Tool Adapter: " + protocolType);
        }
        return adapter;
    }
}
