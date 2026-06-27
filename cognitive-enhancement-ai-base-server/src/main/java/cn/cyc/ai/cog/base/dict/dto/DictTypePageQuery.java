package cn.cyc.ai.cog.base.dict.dto;

import lombok.Data;

/**
 * 字典类型分页查询。
 */
@Data
public class DictTypePageQuery {

    private long current = 1;

    private long size = 10;

    private String keyword;

    private String bizCode;

    private String shareScope;
}
