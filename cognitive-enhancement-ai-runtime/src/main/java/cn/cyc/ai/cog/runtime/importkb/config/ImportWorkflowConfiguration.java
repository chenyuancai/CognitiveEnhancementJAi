package cn.cyc.ai.cog.runtime.importkb.config;

import cn.cyc.ai.cog.core.knowledge.process.config.ImportCapabilityProfile;
import cn.cyc.ai.cog.core.knowledge.process.spi.ImportWorkflowToolkit;
import cn.cyc.ai.cog.core.knowledge.process.workflow.ImportFileParseWorkflowExecutor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 导入工作流 Spring 配置。
 */
@Configuration
@EnableConfigurationProperties(ImportWorkflowProperties.class)
public class ImportWorkflowConfiguration {

    @Bean
    public ImportCapabilityProfile importCapabilityProfile(ImportWorkflowProperties properties) {
        return properties == null ? ImportCapabilityProfile.defaults() : properties.toProfile();
    }

    @Bean
    public ImportFileParseWorkflowExecutor importFileParseWorkflowExecutor(ImportWorkflowToolkit toolkit) {
        return new ImportFileParseWorkflowExecutor(toolkit);
    }
}
