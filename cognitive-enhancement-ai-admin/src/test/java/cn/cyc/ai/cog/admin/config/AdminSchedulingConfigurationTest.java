package cn.cyc.ai.cog.admin.config;

import net.javacrumbs.shedlock.core.LockProvider;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Admin 调度配置测试。
 *
 * @author cyc
 */
class AdminSchedulingConfigurationTest {

    @Test
    void shouldDisableSchedulingInfrastructureWhenPropertyIsFalse() {
        new ApplicationContextRunner()
                .withUserConfiguration(AdminSchedulingConfiguration.class)
                .withBean(DataSource.class, () -> mock(DataSource.class))
                .withPropertyValues("cog.admin.scheduling.enabled=false")
                .run(context -> assertThat(context).doesNotHaveBean(LockProvider.class));
    }
}
