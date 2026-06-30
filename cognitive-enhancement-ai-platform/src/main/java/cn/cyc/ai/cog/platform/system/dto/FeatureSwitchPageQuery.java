package cn.cyc.ai.cog.platform.system.dto;

import cn.cyc.ai.cog.common.page.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * FeatureSwitch分页查询条件
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FeatureSwitchPageQuery extends PageQuery {

    /** 关键词。 */
    private String keyword;
    /** segment。 */
    private String segment;
}
