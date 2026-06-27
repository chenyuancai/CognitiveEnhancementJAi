package cn.cyc.ai.cog.center.model.provider;

import cn.cyc.ai.cog.center.common.CenterPageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 模型提供商分页查询。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ModelProviderPageQuery extends CenterPageQuery {

    private String providerType;
}
