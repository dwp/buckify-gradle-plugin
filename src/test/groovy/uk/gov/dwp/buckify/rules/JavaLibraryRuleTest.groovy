package uk.gov.dwp.buckify.rules

import org.junit.Before
import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.mockito.Mockito.when

class JavaLibraryRuleTest extends RuleTestCase {

    @Before
    void setup() {
        when(dependencyCache.compileDependencies()).thenReturn(dependencies)
        configureResources("src/main/resources")
    }

    @Test
    public void listDeclaredDependenciesThenCommentedOutTransitiveDependencies() {
        configureNonTransitiveDeps("dep2", "dep2", "dep1")
        configureTransitiveDeps("transitiveDep2", "transitiveDep2", "transitiveDep1")

        def underTest = new JavaLibraryRule(this.project, dependencyCache)

        assertEquals underTest.createOutput().toString(), """
java_library(
                name="main",
                autodeps=True,
                srcs=glob(["src/main/java/**/*.java"]),
                resources=glob(['src/main/resources/**/*']),
                resources_root='src/main/resources',
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
