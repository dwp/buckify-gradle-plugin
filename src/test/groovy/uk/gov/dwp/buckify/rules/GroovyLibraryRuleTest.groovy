package uk.gov.dwp.buckify.rules

import org.junit.Before
import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.mockito.Mockito.when

class GroovyLibraryRuleTest extends RuleTestCase {

    @Before
    void setup() {
        when(dependencyCache.compileDependencies()).thenReturn(dependencies)
        configureResources("src/main/resources")
    }

    @Test
    public void listDeclaredDependenciesThenCommentedOutTransitiveDependencies() {
        configureNonTransitiveDeps("dep2", "dep2", "dep1")
        configureTransitiveDeps("transitiveDep2", "transitiveDep2", "transitiveDep1")

        def underTest = new GroovyLibraryRule(project, dependencyCache)

        assertEquals underTest.createOutput().toString(), """
groovy_library(
                name="groovy-main",
                srcs=glob(["src/main/groovy/**/*.groovy", "src/main/groovy/**/*.java"]),
                resources=[],
                deps=[
                    'dep1',
                    'dep2',
                    #transitive deps
                    'transitiveDep1',
                    'transitiveDep2'
                ],
                visibility=["PUBLIC"]
)

"""
    }
}
