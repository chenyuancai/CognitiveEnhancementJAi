package cn.cyc.ai.cog.app.tutoring.support;

import cn.cyc.ai.cog.app.tutoring.dto.AppTutoringStreamEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * 将辅导引擎 SSE 事件映射为前端契约 delta/done/error 帧。
 */
public final class AppTutoringSseAdapter {

    private AppTutoringSseAdapter() {
    }

    public static Map<String, Object> toContractEvent(AppTutoringStreamEvent event) {
        Map<String, Object> frame = new HashMap<>();
        String type = event.getType();
        if ("COMPLETED".equals(type)) {
            frame.put("type", "done");
            frame.put("raw", event.getPayload());
            return frame;
        }
        if ("FAILED".equals(type)) {
            frame.put("type", "error");
            frame.put("message", event.getPayload() == null ? "failed" : event.getPayload().get("message"));
            return frame;
        }
        frame.put("type", "delta");
        Object text = event.getPayload() == null ? null : event.getPayload().get("text");
        if (text == null && event.getPayload() != null) {
            text = event.getPayload().get("message");
        }
        frame.put("text", text == null ? "" : String.valueOf(text));
        return frame;
    }
}
