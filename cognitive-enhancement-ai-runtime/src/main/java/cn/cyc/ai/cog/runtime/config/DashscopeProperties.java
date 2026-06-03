package cn.cyc.ai.cog.runtime.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * 百炼 Dashscope 配置。
 *
 * <p>从环境变量或系统属性读取 API Key。
 *
 * <p>配置方式（优先级从高到低）：
 * <ol>
 *   <li>环境变量 {@code DASHSCOPE_API_KEY}</li>
 *   <li>JVM 系统属性 {@code -Ddashscope.api-key=xxx}</li>
 *   <li>Spring 配置文件 {@code dashscope.api-key=xxx}</li>
 * </ol>
 *
 * @author cyc
 */
@Configuration
public class DashscopeProperties {

    /**
     * 百炼 API Key。
     */
    @Value("${DASHSCOPE_API_KEY:${dashscope.api-key:}}")
    private String apiKey;

    /**
     * 获取百炼 API Key。
     *
     * @return API Key
     */
    public String getApiKey() {
        return apiKey;
    }
}
