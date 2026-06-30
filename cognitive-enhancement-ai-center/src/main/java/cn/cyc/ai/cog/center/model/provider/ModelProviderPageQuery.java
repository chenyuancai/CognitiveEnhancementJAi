package cn.cyc.ai.cog.center.model.provider;

import cn.cyc.ai.cog.center.common.CenterPageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 模型提供商分页查询。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ModelProviderPageQuery extends CenterPageQuery {

    /** 提供者类型。 */
    private String providerType;
}
