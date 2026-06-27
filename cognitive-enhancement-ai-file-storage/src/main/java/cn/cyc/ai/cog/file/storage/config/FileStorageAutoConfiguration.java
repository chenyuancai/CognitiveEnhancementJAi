package cn.cyc.ai.cog.file.storage.config;

import cn.cyc.ai.cog.file.storage.spi.DiskFileStorageStrategy;
import cn.cyc.ai.cog.file.storage.spi.FileStorageStrategy;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * 文件对象存储自动装配：引入本模块依赖即可注册 {@link FileStorageStrategy}。
 */
@AutoConfiguration
@EnableConfigurationProperties(FileStorageProperties.class)
public class FileStorageAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(FileStorageStrategy.class)
    @ConditionalOnProperty(name = "cog.file.storage-type", havingValue = "disk", matchIfMissing = true)
    public FileStorageStrategy diskFileStorageStrategy(FileStorageProperties properties) {
        return new DiskFileStorageStrategy(properties);
    }
}
