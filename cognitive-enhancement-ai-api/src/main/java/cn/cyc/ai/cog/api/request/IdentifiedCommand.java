package cn.cyc.ai.cog.api.request;

/**
 * 仅含资源主键的写操作请求（如下线、取消、标记已读）。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record IdentifiedCommand(Long id) {
}
