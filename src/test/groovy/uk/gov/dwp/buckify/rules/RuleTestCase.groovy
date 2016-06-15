package uk.gov.dwp.buckify.rules

import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionContainer
import org.junit.Before
import uk.gov.dwp.buckify.BuckifyExtension
import uk.gov.dwp.buckify.dependencies.BuckDependency
import uk.gov.dwp.buckify.dependencies.Dependencies
import uk.gov.dwp.buckify.dependencies.DependencyCache

import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

abstract class RuleTestCase {

    def project = mock(Project)
    def dependencies = mock(Dependencies)
    def dependencyCache = mock(DependencyCache)
    def preExistingRules = mock(PreExistingRules)
    def extensionContainer = mock(ExtensionContainer)
    def resourcesDir = mock(File)

    @Before
    public void setUp() {
        when(project.extensions).thenReturn(extensionContainer)
        when(extensionContainer.findByType(BuckifyExtension)).thenReturn(new BuckifyExtension())
    }

    def configureResources(String directory) {
        when(project.file(directory)).thenReturn(resourcesDir)
    }

    def configureTransitiveDeps(String... deps) {
        when(dependencies.transitiveDependencies).thenReturn(deps.collect({ new TestDependency(it) }) as Set)
    }

    def configureNonTransitiveDeps(String... deps) {
        when(dependencies.nonTransitiveDependencies()).thenReturn(deps.collect({ new TestDependency(it) }) as Set)
    }

    def configureSpecificDeps(String... deps) {
        when(dependencies.configSpecificDependencies).thenReturn(deps.collect({ new TestDependency(it) }) as Set)
    }

    private static class TestDependency implements BuckDependency {

        TestDependency(path) {
            this.path = path
        }
    }
}
