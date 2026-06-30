package cn.cyc.ai.cog.app.practice.dto;

import lombok.Data;

import java.util.Map;

/**
 * 创建练习会话请求。
 *
 * @author cyc
 * @date 2026/6/29
 */
@Data
public class AppPracticeCreateSessionRequest {

    /** 题目数量（默认 2，上限 30） */
    private Integer questionCount;

    /** 题型开关：choice / essay */
    private Map<String, Boolean> questionTypes;

    /** 来源内容 ID（可选） */
    private String sourceId;
}
