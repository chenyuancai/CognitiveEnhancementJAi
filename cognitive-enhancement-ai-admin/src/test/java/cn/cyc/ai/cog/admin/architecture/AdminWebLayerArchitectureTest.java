package cn.cyc.ai.cog.admin.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * admin 模块 Web 层架构守门测试。
 */
class AdminWebLayerArchitectureTest {

    private static final JavaClasses ADMIN_CLASSES = new ClassFileImporter()
            .importPackages("cn.cyc.ai.cog.admin");

    @Test
    void adminWebShouldNotDependOnPlatformEntities() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..admin..web..")
                .should().dependOnClassesThat().resideInAPackage("..platform..entity..")
                .because("web 层应使用 VO/DTO，禁止直接依赖 platform Entity");

        rule.check(ADMIN_CLASSES);
    }

    @Test
    void adminWebShouldNotDependOnAdminRbacEntities() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..admin..web..")
                .should().dependOnClassesThat().resideInAPackage("..admin.rbac.entity..")
                .because("web 层应使用 VO，禁止直接依赖 rbac Entity");

        rule.check(ADMIN_CLASSES);
    }

    /**
     * admin.operation.service 不得直接依赖 Mapper/Entity，应委托 platform repository。
     */
    @Test
    void adminOperationServiceShouldNotDependOnMappers() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..admin.operation.service..")
                .should().dependOnClassesThat().haveNameMatching(".*Mapper")
                .because("运营聚合服务应通过 platform repository 访问持久化");

        rule.check(ADMIN_CLASSES);
    }

    @Test
    void adminOperationServiceShouldNotDependOnEntities() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..admin.operation.service..")
                .should().dependOnClassesThat().haveNameMatching(".*Entity")
                .because("运营聚合服务应使用 platform domain/DTO");

        rule.check(ADMIN_CLASSES);
    }

    /**
     * admin.rbac.service 不得直接依赖 Mapper，应委托 rbac repository。
     */
    @Test
    void adminRbacServiceShouldNotDependOnMappers() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..admin.rbac.service..")
                .should().dependOnClassesThat().haveNameMatching(".*Mapper")
                .because("RBAC 服务应通过 repository 访问持久化");

        rule.check(ADMIN_CLASSES);
    }

    /**
     * admin.auth.service 不得直接依赖 Mapper，应委托 platform/admin repository。
     */
    @Test
    void adminAuthServiceShouldNotDependOnMappers() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..admin.auth.service..")
                .should().dependOnClassesThat().haveNameMatching(".*Mapper")
                .because("认证聚合服务应通过 repository 访问持久化");

        rule.check(ADMIN_CLASSES);
    }
}
