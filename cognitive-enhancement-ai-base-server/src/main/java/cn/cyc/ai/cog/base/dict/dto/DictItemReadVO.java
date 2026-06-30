package cn.cyc.ai.cog.base.dict.dto;

import lombok.Data;

/**
 * 公共字典项读取结果（下拉/Tag）。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class DictItemReadVO {

    /** 值。 */
    private String value;

    /** label。 */
    private String label;

    /** enLabel。 */
    private String enLabel;

    /** sort。 */
    private Integer sort;
}
