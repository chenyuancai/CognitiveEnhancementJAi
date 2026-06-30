package cn.cyc.ai.cog.app.practice.support;

import cn.cyc.ai.cog.app.practice.dto.AppPracticeQuestionVO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 练习题目模板工厂（首期静态题，后续可接知识库元数据）。
 *
 * @author cyc
 * @date 2026/6/29
 */
public final class PracticeQuestionFactory {

    public static final String CHOICE_CORRECT_KEY = "B";

    private PracticeQuestionFactory() {
    }

    public static List<AppPracticeQuestionVO> buildQueue(int questionCount, boolean choice, boolean essay) {
        List<AppPracticeQuestionVO> queue = new ArrayList<>();
        for (int i = 0; i < questionCount; i++) {
            if (choice && (i % 2 == 0 || !essay)) {
                queue.add(choiceQuestion(i + 1));
            } else if (essay) {
                queue.add(essayQuestion(i + 1));
            } else {
                queue.add(choiceQuestion(i + 1));
            }
        }
        if (queue.isEmpty()) {
            queue.add(choiceQuestion(1));
        }
        return queue;
    }

    public static AppPracticeQuestionVO choiceQuestion(int num) {
        AppPracticeQuestionVO vo = new AppPracticeQuestionVO();
        vo.setId("q-choice-" + num);
        vo.setType("choice");
        vo.setStem("根据《民法典》规定，以下哪项属于合同无效的情形？");
        vo.setOptions(List.of(
                option("A", "一方以欺诈手段订立的合同"),
                option("B", "行为人与相对人恶意串通损害他人合法权益的合同", true),
                option("C", "基于重大误解订立的合同"),
                option("D", "显失公平的合同")));
        return vo;
    }

    public static AppPracticeQuestionVO essayQuestion(int num) {
        AppPracticeQuestionVO vo = new AppPracticeQuestionVO();
        vo.setId("q-essay-" + num);
        vo.setType("essay");
        vo.setStem("请简述合同违约责任的主要承担方式，并说明适用情形。");
        return vo;
    }

    public static boolean isChoiceCorrect(String answerKey) {
        return CHOICE_CORRECT_KEY.equalsIgnoreCase(answerKey);
    }

    public static int choiceScore(String answerKey) {
        return isChoiceCorrect(answerKey) ? 100 : 20;
    }

    private static Map<String, Object> option(String key, String text) {
        return option(key, text, false);
    }

    private static Map<String, Object> option(String key, String text, boolean correct) {
        return Map.of("key", key, "text", text, "correct", correct);
    }
}
