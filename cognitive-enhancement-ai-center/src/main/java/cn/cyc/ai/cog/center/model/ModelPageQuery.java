package cn.cyc.ai.cog.center.model;

import cn.cyc.ai.cog.center.common.CenterPageQuery;

/**
 * 模型分页查询参数。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public class ModelPageQuery extends CenterPageQuery {

    /**
     * 模型提供方编码。
     */
    private String providerCode;

    /**
     * 模型类型。
     */
    private String modelType;

    /**
     * 获取提供者编码。
     * @return 提供者编码
     */
    public String getProviderCode() {
        return providerCode;
    }

    /**
     * 设置提供者编码。
     *
     * @param providerCode 提供者编码
     */
    public void setProviderCode(String providerCode) {
        this.providerCode = providerCode;
    }

    /**
     * 获取模型类型。
     * @return 模型类型
     */
    public String getModelType() {
        return modelType;
    }

    /**
     * 设置模型类型。
     *
     * @param modelType 模型类型
     */
    public void setModelType(String modelType) {
        this.modelType = modelType;
    }
}
