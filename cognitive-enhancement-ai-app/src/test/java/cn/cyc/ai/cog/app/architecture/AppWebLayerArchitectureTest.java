package cn.cyc.ai.cog.app.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * app 模块 Web 层架构守门测试。
 */
class AppWebLayerArchitectureTest {

    private static final JavaClasses APP_CLASSES = new ClassFileImporter()
            .importPackages("cn.cyc.ai.cog.app");

    /**
     * app.web 控制器不得直接依赖 platform Entity。
     */
    @Test
    void appWebShouldNotDependOnPlatformEntities() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..app.web..")
                .should().dependOnClassesThat().resideInAPackage("..platform..entity..")
                .because("web 层应使用 VO/DTO，禁止直接依赖 Entity");

        rule.check(APP_CLASSES);
    }
}
