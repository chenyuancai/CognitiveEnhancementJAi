package cn.cyc.ai.cog.runtime.tool.local;

import cn.cyc.ai.cog.runtime.spi.LocalToolHandler;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 本地 Tool 注册表。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Component
public class LocalToolRegistry {

    /**
     * 处理器索引。
     */
    private final Map<String, LocalToolHandler> handlers;

    /**
     * 构造本地 Tool 注册表。
     *
     * @param handlers 已注册的处理器列表
     */
    public LocalToolRegistry(List<LocalToolHandler> handlers) {
        this.handlers = handlers.stream()
                .collect(Collectors.toMap(LocalToolHandler::implRef, Function.identity(), (left, right) -> right));
    }

    /**
     * 按实现引用查找处理器。
     *
     * @param implRef 实现引用编码
     * @return 匹配到的处理器
     */
    public Optional<LocalToolHandler> find(String implRef) {
        return Optional.ofNullable(handlers.get(implRef));
    }
}
