package cn.cyc.ai.cog.app.tutoring.dto;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * C 端 AI 助手对话选项。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class AppTutoringChatOptions {

    /** 是否流式返回（预留）。 */
    private boolean stream;

    /** 用户偏好（弱信号，不参与策略强决策）。 */
    private Map<String, Object> preferences = new HashMap<>();
}
