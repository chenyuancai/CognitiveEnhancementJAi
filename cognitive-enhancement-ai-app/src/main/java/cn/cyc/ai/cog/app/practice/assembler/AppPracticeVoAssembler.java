package cn.cyc.ai.cog.app.practice.assembler;

import cn.cyc.ai.cog.app.practice.dto.AppPracticeChoiceResultVO;
import cn.cyc.ai.cog.app.practice.dto.AppPracticeDebriefVO;
import cn.cyc.ai.cog.app.practice.dto.AppPracticeEssaySubmitVO;
import cn.cyc.ai.cog.app.practice.dto.AppPracticeQuestionVO;
import cn.cyc.ai.cog.app.practice.dto.AppPracticeSessionVO;
import cn.cyc.ai.cog.app.practice.support.PracticeQuestionFactory;
import cn.cyc.ai.cog.platform.practice.entity.PracticeAnswerEntity;
import cn.cyc.ai.cog.platform.practice.entity.PracticeSessionEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 练习域 Entity / 领域结果 → 契约 VO 转换器。
 *
 * @author cyc
 * @date 2026/6/29
 */
@Component
public class AppPracticeVoAssembler {

    /**
     * 组装创建会话响应。
     *
     * @param session   持久化会话
     * @param questions 题目队列
     * @return 会话 VO
     */
    public AppPracticeSessionVO toSessionVo(PracticeSessionEntity session, List<AppPracticeQuestionVO> questions) {
        AppPracticeSessionVO vo = new AppPracticeSessionVO();
        vo.setSessionId(session.getSessionCode());
        vo.setQuestionCount(questions.size());
        vo.setQuestions(questions);
        return vo;
    }

    /**
     * 组装选择题作答结果。
     *
     * @param sessionCode 会话编码
     * @param answerKey   用户选项
     * @param answer      作答记录
     * @return 选择题结果 VO
     */
    public AppPracticeChoiceResultVO toChoiceResult(String sessionCode, String answerKey,
                                                    PracticeAnswerEntity answer) {
        AppPracticeChoiceResultVO vo = new AppPracticeChoiceResultVO();
        vo.setSessionId(sessionCode);
        vo.setCorrect(PracticeQuestionFactory.isChoiceCorrect(answerKey));
        vo.setScore(PracticeQuestionFactory.choiceScore(answerKey));
        vo.setCorrectAnswer(PracticeQuestionFactory.CHOICE_CORRECT_KEY);
        vo.setExplanation("恶意串通损害他人合法权益的合同无效。");
        vo.setAnswerId(answer.getId());
        return vo;
    }

    /**
     * 组装问答题提交响应（待 AI 评分）。
     *
     * @param sessionCode 会话编码
     * @param answer      作答记录
     * @return 问答题提交 VO
     */
    public AppPracticeEssaySubmitVO toEssaySubmit(String sessionCode, PracticeAnswerEntity answer) {
        AppPracticeEssaySubmitVO vo = new AppPracticeEssaySubmitVO();
        vo.setSessionId(sessionCode);
        vo.setAnswerId(answer.getId());
        vo.setJobId("score_" + answer.getId());
        return vo;
    }

    /**
     * 组装练习复盘摘要。
     *
     * @param sessionCode   会话编码
     * @param session       会话实体
     * @param answerSummary 作答统计
     * @return 复盘 VO
     */
    public AppPracticeDebriefVO toDebrief(String sessionCode, PracticeSessionEntity session,
                                        Map<String, Object> answerSummary) {
        AppPracticeDebriefVO vo = new AppPracticeDebriefVO();
        vo.setSessionId(sessionCode);
        vo.setTitle(session.getTitle());
        vo.setAnswerSummary(answerSummary);
        return vo;
    }
}
