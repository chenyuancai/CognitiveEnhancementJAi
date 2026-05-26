package cn.cyc.ai.cog.core.metadata;

import cn.cyc.ai.cog.core.metadata.type.CommonStatus;

/**
 * 核心元数据对象统一抽象。
 */
public interface MetadataDefinition {

    /**
     * 返回元数据对象稳定编码。
     */
    String code();

    /**
     * 返回元数据对象展示名称。
     */
    String name();

    /**
     * 返回当前状态。
     */
    CommonStatus status();
}
