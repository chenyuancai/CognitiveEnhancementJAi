package cn.cyc.ai.cog.center.model;

import cn.cyc.ai.cog.center.common.CenterPageQuery;

/**
 * 模型分页查询参数。
 *
 * @author cyc
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

    public String getProviderCode() {
        return providerCode;
    }

    public void setProviderCode(String providerCode) {
        this.providerCode = providerCode;
    }

    public String getModelType() {
        return modelType;
    }

    public void setModelType(String modelType) {
        this.modelType = modelType;
    }
}
