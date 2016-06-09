package uk.gov.dwp.buckify.rules

import org.junit.Before
import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.mockito.Mockito.when

class JavaTestRuleTest extends RuleTestCase {

    @Before
    void setup() {
        when(dependencyCache.testCompileDependencies()).thenReturn(dependencies)
        configureResources("src/test/resources")
    }

    @Test
    public void listConfigSpecificDependencies() {
        configureSpecificDeps("dep2", "dep1")

        def underTest = new JavaTestRule(this.project, dependencyCache)

        assertEquals underTest.createOutput().toString(), """
java_test(
                name="test",
                autodeps=True,
                source_under_test=[":main"],
                resources=[],
                deps=["dep1", "dep2"],
                visibility=["PUBLIC"]
)

"""
    }
}
