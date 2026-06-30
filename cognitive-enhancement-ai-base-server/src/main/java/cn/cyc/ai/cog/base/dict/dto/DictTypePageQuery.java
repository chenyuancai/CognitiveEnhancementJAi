package cn.cyc.ai.cog.base.dict.dto;

import lombok.Data;

/**
 * 字典类型分页查询。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class DictTypePageQuery {

    /** current。 */
    private long current = 1;

    /** 大小。 */
    private long size = 10;

    /** 关键词。 */
    private String keyword;

    /** biz编码。 */
    private String bizCode;

    /** shareScope。 */
    private String shareScope;
}
