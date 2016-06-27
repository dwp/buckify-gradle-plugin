package uk.gov.dwp.buckify.rules

import org.junit.Before
import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.mockito.Mockito.when

class GroovyTestRuleTest extends RuleTestCase {

    @Before
    void setup() {
        when(dependencyCache.testCompileDependencies()).thenReturn(dependencies)
        configureResources("src/test/resources")
    }

    @Test
    public void listConfigSpecificDependencies() {
        configureNonTransitiveDeps("dep2", "dep2", "dep1")
        configureTransitiveDeps("transitiveDep2", "transitiveDep2", "transitiveDep1")

        def underTest = new GroovyTestRule(this.project, dependencyCache)

        assertEquals underTest.createOutput().toString(), """
groovy_test(
                name="groovy-test",
                srcs=glob(["src/main/groovy/**/*.groovy", "src/main/groovy/**/*.java"]),
                resources=glob(['src/test/resources/**/*']),
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
