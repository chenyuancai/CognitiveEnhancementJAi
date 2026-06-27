package cn.cyc.ai.cog.admin.system.service;

import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统健康探针：数据库实测 + 其余组件占位。
 */
@Service
public class SystemHealthService {

    private final DataSource dataSource;

    public SystemHealthService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Map<String, Object> buildReport() {
        String checkedAt = Instant.now().toString();
        List<Map<String, Object>> services = new ArrayList<>();
        services.add(probeDatabase(checkedAt));
        services.add(stubService("cache", "UNKNOWN", "Redis 探针待接入", checkedAt));
        services.add(stubService("queue", "UNKNOWN", "任务队列探针待接入", checkedAt));
        services.add(stubService("search", "UNKNOWN", "搜索服务探针待接入", checkedAt));
        services.add(stubService("storage", "UNKNOWN", "对象存储探针待接入", checkedAt));
        services.add(stubService("email", "UNKNOWN", "邮件服务探针待接入", checkedAt));
        services.add(stubService("payment", "UP", "支付回调占位已就绪", checkedAt));

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", aggregateStatus(services));
        body.put("service", "cognitive-enhancement-ai-admin");
        body.put("checkedAt", checkedAt);
        body.put("timestamp", checkedAt);
        body.put("services", services);
        return body;
    }

    private Map<String, Object> probeDatabase(String checkedAt) {
        long start = System.currentTimeMillis();
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("key", "database");
        item.put("checkedAt", checkedAt);
        try (Connection connection = dataSource.getConnection()) {
            connection.isValid(2);
            long latency = System.currentTimeMillis() - start;
            item.put("status", latency <= 200 ? "UP" : "WARN");
            item.put("latencyMs", latency);
            item.put("note", "MySQL 连接正常");
        } catch (Exception ex) {
            item.put("status", "DOWN");
            item.put("latencyMs", System.currentTimeMillis() - start);
            item.put("note", "数据库不可用：" + ex.getMessage());
        }
        return item;
    }

    private Map<String, Object> stubService(String key, String status, String note, String checkedAt) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("key", key);
        item.put("status", status);
        item.put("note", note);
        item.put("checkedAt", checkedAt);
        return item;
    }

    private String aggregateStatus(List<Map<String, Object>> services) {
        boolean anyDown = services.stream().anyMatch(s -> "DOWN".equals(s.get("status")));
        if (anyDown) {
            return "DOWN";
        }
        boolean anyWarn = services.stream().anyMatch(s -> "WARN".equals(s.get("status")));
        if (anyWarn) {
            return "WARN";
        }
        return "UP";
    }
}
