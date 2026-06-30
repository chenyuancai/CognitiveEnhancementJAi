package cn.cyc.ai.cog.app.tutoring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * 学习辅导异步执行线程池配置。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Configuration
@EnableAsync
public class AppTutoringAsyncConfiguration {

    /**
     * 学习辅导聊天异步任务线程池。
     *
     * @return 任务执行器
     */
    /**
     * 执行C端辅导ChatExecutor。
     * @return 执行结果
     */
    @Bean(name = "appTutoringChatExecutor")
    public TaskExecutor appTutoringChatExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setThreadNamePrefix("app-tutoring-chat-");
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(16);
        executor.setQueueCapacity(200);
        executor.initialize();
        return executor;
    }
}
