package cn.cyc.ai.cog.platform.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * platform 模块分层架构守门测试。
 * <p>
 * 对已 repository 化的 service 包启用 Mapper/Entity 依赖禁令。
 * </p>
 */
class PlatformLayerArchitectureTest {

    private static final String[] REPOSITORY_READY_PACKAGES = {
            "cn.cyc.ai.cog.platform.knowledge.service",
            "cn.cyc.ai.cog.platform.operations.service",
            "cn.cyc.ai.cog.platform.system.service",
            "cn.cyc.ai.cog.platform.membership.service",
            "cn.cyc.ai.cog.platform.quota.service",
            "cn.cyc.ai.cog.platform.account.service",
            "cn.cyc.ai.cog.platform.iam.service",
            "cn.cyc.ai.cog.platform.org.service",
            "cn.cyc.ai.cog.platform.billing.service"
    };

    private static final JavaClasses SERVICE_CLASSES = new ClassFileImporter()
            .importPackages(REPOSITORY_READY_PACKAGES);

    @Test
    void repositoryReadyServicesShouldNotDependOnMappers() {
        ArchRule rule = noClasses()
                .that().resideInAnyPackage(
                        "..platform.knowledge.service..",
                        "..platform.operations.service..",
                        "..platform.system.service..",
                        "..platform.membership.service..",
                        "..platform.quota.service..",
                        "..platform.account.service..",
                        "..platform.iam.service..",
                        "..platform.org.service..",
                        "..platform.billing.service..")
                .should().dependOnClassesThat().haveNameMatching(".*Mapper")
                .because("service 层应通过 repository 适配器访问持久化，禁止直接依赖 Mapper");

        rule.check(SERVICE_CLASSES);
    }

    @Test
    void repositoryReadyServicesShouldNotDependOnEntities() {
        ArchRule rule = noClasses()
                .that().resideInAnyPackage(
                        "..platform.knowledge.service..",
                        "..platform.operations.service..",
                        "..platform.system.service..",
                        "..platform.membership.service..",
                        "..platform.quota.service..",
                        "..platform.account.service..",
                        "..platform.iam.service..",
                        "..platform.org.service..",
                        "..platform.billing.service..")
                .should().dependOnClassesThat().haveNameMatching(".*Entity")
                .because("service 层应使用 domain/DTO，禁止直接依赖 Entity");

        rule.check(SERVICE_CLASSES);
    }
}
