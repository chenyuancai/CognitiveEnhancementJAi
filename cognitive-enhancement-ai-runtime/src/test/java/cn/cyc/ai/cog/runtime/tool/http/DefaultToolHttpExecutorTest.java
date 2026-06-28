package cn.cyc.ai.cog.runtime.tool.http;

import cn.cyc.ai.cog.runtime.api.ToolHttpRequest;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opentest4j.TestAbortedException;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DefaultToolHttpExecutorTest {

    private HttpServer server;
    private int port;
    private DefaultToolHttpExecutor executor;

    @BeforeEach
    void setUp() throws IOException {
        try {
            server = HttpServer.create(new InetSocketAddress(0), 0);
        } catch (IOException ex) {
            if (ex.getMessage() != null && ex.getMessage().contains("Operation not permitted")) {
                throw new TestAbortedException("当前环境不允许绑定本地测试端口", ex);
            }
            throw ex;
        }
        server.createContext("/echo", exchange -> {
            byte[] response = "{\"answer\":\"http tool ok\"}".getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.length);
            try (OutputStream outputStream = exchange.getResponseBody()) {
                outputStream.write(response);
            }
        });
        server.start();
        port = server.getAddress().getPort();
        executor = new DefaultToolHttpExecutor();
    }

    @AfterEach
    void tearDown() {
        if (server != null) {
            server.stop(0);
        }
    }

    @Test
    void execute_shouldReturnHttpResponse() {
        var response = executor.execute(new ToolHttpRequest(
                "http://127.0.0.1:" + port + "/echo",
                "POST",
                Map.of("Content-Type", "application/json"),
                "{\"question\":\"hello\"}",
                Duration.ofSeconds(5)
        ));

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("http tool ok"));
        assertTrue(response.latencyMs() >= 0);
    }
}
