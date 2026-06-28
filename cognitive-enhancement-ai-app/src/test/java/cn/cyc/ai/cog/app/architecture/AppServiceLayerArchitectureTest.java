package cn.cyc.ai.cog.app.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * app 模块 Service 层架构守门测试。
 */
class AppServiceLayerArchitectureTest {

    private static final JavaClasses APP_CLASSES = new ClassFileImporter()
            .importPackages("cn.cyc.ai.cog.app");

    /**
     * app.service 不得直接依赖 platform Entity。
     */
    @Test
    void appServiceShouldNotDependOnPlatformEntities() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..app.service..")
                .should().dependOnClassesThat().resideInAPackage("..platform..entity..")
                .because("service 层应使用 platform DTO/Service，禁止直接依赖 Entity");

        rule.check(APP_CLASSES);
    }

    /**
     * app.service 不得直接依赖 platform Mapper。
     */
    @Test
    void appServiceShouldNotDependOnPlatformMappers() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..app.service..")
                .should().dependOnClassesThat().haveNameMatching(".*Mapper")
                .because("service 层应通过 platform Service 访问数据，禁止直接依赖 Mapper");

        rule.check(APP_CLASSES);
    }
}
