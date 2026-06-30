package cn.cyc.ai.cog.app.practice.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 练习题目 VO。
 *
 * @author cyc
 * @date 2026/6/29
 */
@Data
public class AppPracticeQuestionVO {

    /** 题目 ID */
    private String id;

    /** 题型：choice / essay */
    private String type;

    /** 题干 */
    private String stem;

    /** 选择题选项（essay 为空） */
    private List<Map<String, Object>> options;
}
