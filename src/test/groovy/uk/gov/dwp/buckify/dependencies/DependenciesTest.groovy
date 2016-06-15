package uk.gov.dwp.buckify.dependencies

import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Test
import uk.gov.dwp.buckify.BuckifyExtension
import uk.gov.dwp.buckify.rules.PreExistingRules

import static org.mockito.Mockito.mock

class DependenciesTest {
    private final Project testProject = createProject()
    private final BuckifyExtension extension = testProject.extensions.findByType(BuckifyExtension)
    private final DependencyFactory factory = new  DependencyFactory(mock(PreExistingRules), extension)

    @Before
    public void setUp() {
        extension.nomenclature = {artifact -> artifact.name}

        Project childProject = ProjectBuilder.builder().withName("child").withParent(testProject).build()
        childProject.plugins.apply('java')
        childProject.dependencies {}

        testProject.dependencies {
            compile project(path: ':child')
            compile 'commons-lang:commons-lang:2.6'
            compile 'joda-time:joda-time:2.2'
            compile group: 'info.cukes', name: 'cucumber-core', version: '1.1.5'
            testCompile group: 'junit', name: 'junit', version: '4.11'
        }
    }

    @Test
    void findProjectDependencies() {
        def dependencies = Dependencies.factory(configuration("compile"), extension, factory)

        assert dependencies.projectDependencies.size() == 1
        assert dependencies.projectDependencies.collect({ it.name }) == ["child"]
    }

    @Test
    void findDeclaredExternalDependencies() {
        def dependencies = Dependencies.factory(configuration("compile"), extension, factory)

        assert dependencies.declaredExternalDependencies.size() == 3
        assert dependencies.declaredExternalDependencies.collect({ it.name }).containsAll(["commons-lang", "joda-time", "cucumber-core"])
    }

    @Test
    void findDeclaredTransitiveDependencies() {
        def dependencies = Dependencies.factory(configuration("compile"), extension, factory)

        assert dependencies.transitiveDependencies.size() == 3
        assert dependencies.transitiveDependencies.collect({ it.name }).containsAll(["cucumber-html", "cucumber-jvm-deps", "gherkin"])
        assert dependencies.transitiveDependencies.disjoint(dependencies.nonTransitiveDependencies())
    }

    @Test
    void findConfigSpecificDependencies() {
        def dependencies = Dependencies.factory(configuration("testCompile"), extension, factory)

        assert dependencies.configSpecificDependencies.size() == 1
        assert dependencies.configSpecificDependencies.collect({ it.name }) == ["junit"]
    }

    private Project createProject() {
        def myProject = ProjectBuilder.builder().build()
        myProject.extensions.create("buckify", BuckifyExtension)
        myProject.plugins.apply('java')
        myProject.plugins.apply('groovy')
        myProject.repositories {
            mavenLocal()
            maven { url 'http://bld1.infra.uk1.uc:8081/nexus/content/repositories/releases' }
            maven { url 'http://bld1.infra.uk1.uc:8081/nexus/content/groups/public/' }
            maven { url 'http://bld1.infra.uk1.uc:8081/nexus/content/repositories/snapshots' }
            maven { url 'https://repo.gradle.org/gradle/libs' }
        }
        myProject
    }

    private Configuration configuration(String name) {
        testProject.configurations.findByName(name)
    }
}
