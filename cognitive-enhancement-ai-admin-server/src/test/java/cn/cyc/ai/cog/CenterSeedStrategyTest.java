package cn.cyc.ai.cog;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 种子数据策略测试：内存模式仅加载 Demo 初始化器。
 *
 * @author cyc
 */
@SpringBootTest
@TestPropertySource(properties = {
        "cog.persistence.enabled=false",
        "cog.seed.enabled=true"
})
class CenterSeedStrategyTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void shouldLoadDemoSeedRunnerWhenPersistenceDisabled() {
        assertTrue(applicationContext.containsBean("centerSeedRunner"));
        assertFalse(applicationContext.containsBean("adminSeedRunner"));
    }
}
