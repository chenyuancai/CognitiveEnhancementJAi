package cn.cyc.ai.cog.app.contract;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * SSE JSON 帧写入（契约 events: data: {...}）。
 *
 * @author cyc
 * @date 2026/6/29
 */
public final class AppSseJsonWriter {

    private AppSseJsonWriter() {
    }

    /**
     * 写入单条 SSE data 帧并 flush。
     */
    public static void write(OutputStream outputStream, ObjectMapper objectMapper, Map<String, Object> payload)
            throws IOException {
        outputStream.write(("data: " + objectMapper.writeValueAsString(payload) + "\n\n")
                .getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }
}
