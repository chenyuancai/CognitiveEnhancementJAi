package cn.cyc.ai.cog.app.dto;

import lombok.Data;

/**
 * C 端站内信查询参数。
 */
@Data
public class AppInAppMessageQuery {

    /** 已读状态筛选，可选。 */
    private Boolean read;
}
