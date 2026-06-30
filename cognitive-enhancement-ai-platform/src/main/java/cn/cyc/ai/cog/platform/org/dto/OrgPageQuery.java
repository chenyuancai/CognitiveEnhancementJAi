package cn.cyc.ai.cog.platform.org.dto;

import lombok.Data;

/**
 * Org分页查询条件
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class OrgPageQuery {

    /** current。 */
    private long current = 1;
    /** 大小。 */
    private long size = 20;
    /** 关键词。 */
    private String keyword;
    /** segment。 */
    private String segment;
}
