package cn.cyc.ai.cog.center.model;

import cn.cyc.ai.cog.core.metadata.type.CommonStatus;

/**
 * 模型管理返回对象。
 *
 * @param providerCode      提供商编码
 * @param providerName      提供商名称
 * @param modelCode         模型编码
 * @param modelName         模型名称
 * @param modelType         模型类型
 * @param endpoint          调用地址
 * @param credentialRef     凭证引用
 * @param timeoutMs         超时时间
 * @param retryTimes        重试次数
 * @param status            启用状态
 * @param routePriority     路由优先级
 * @param fallbackModelCode 降级模型编码
 * @author cyc
 */
public record ModelResult(
        String providerCode,
        String providerName,
        String modelCode,
        String modelName,
        String modelType,
        String endpoint,
        String credentialRef,
        int timeoutMs,
        int retryTimes,
        CommonStatus status,
        int routePriority,
        String fallbackModelCode
) {
}
