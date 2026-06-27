package cn.cyc.ai.cog.base.dict.dto;

import lombok.Data;

/**
 * 公共字典项读取结果（下拉/Tag）。
 */
@Data
public class DictItemReadVO {

    private String value;

    private String label;

    private String enLabel;

    private Integer sort;
}
