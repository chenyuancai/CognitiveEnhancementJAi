package cn.cyc.ai.cog.app.dto;

import lombok.Data;

/**
 * C 端 Banner 查询参数。
 */
@Data
public class AppBannerQuery {

    /** 展示位，默认 HOME_TOP。 */
    private String position;
}
