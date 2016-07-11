package uk.gov.dwp.buckify.rules

import org.junit.Test
import uk.gov.dwp.buckify.dependencies.ArtifactDependency

import static org.junit.Assert.assertEquals
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

class FromNexusRuleTest extends RuleTestCase {

    @Test
    public void createOutput() {
        def underTest = new FromNexusRule("name", "version", "group", "artifact", "sha1")

        assertEquals "from_nexus(name='name', version='version', ga='group:artifact', sha1='sha1')\n", underTest.createOutput().toString()
    }

    @Test
    public void generateRuleWhenOneDoesntAlreadyExists() {
        def dependency = mock(ArtifactDependency)

        when(dependency.name).thenReturn("ruleName")
        when(dependencyCache.externalDependenciesForAllConfigurations()).thenReturn([dependency].toSet())

        def rules = PreBuiltJarRule.generator(project, dependencyCache)
        assert rules.size() == 1
        assert rules.first().name == "ruleName"
    }

    @Test
    public void doNotGenerateRuleWhenOneAlreadyExists() {
        def dependency = mock(ArtifactDependency)

        when(dependency.name).thenReturn("ruleName")
        when(dependencyCache.externalDependenciesForAllConfigurations()).thenReturn([dependency].toSet())
        when(dependencyCache.rulesExist("ruleName")).thenReturn(true)

        assert PreBuiltJarRule.generator(project, dependencyCache).isEmpty()
    }
}