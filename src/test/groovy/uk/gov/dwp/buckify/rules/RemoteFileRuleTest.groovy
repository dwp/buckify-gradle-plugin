package uk.gov.dwp.buckify.rules

import org.junit.Test
import uk.gov.dwp.buckify.dependencies.ArtifactDependency

import static org.junit.Assert.assertEquals
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

class RemoteFileRuleTest extends RuleTestCase {

    @Test
    public void createOutput() {
        def dependency = mock(ArtifactDependency)
        when(dependency.name).thenReturn("ruleName")
        when(dependency.filename).thenReturn("filename")
        when(dependency.identifier).thenReturn("url")
        when(dependency.sha1).thenReturn("sha1")

        def underTest = new RemoteFileRule(dependency)

        assertEquals "remote_file(name='ruleName-mvn', out='filename', url='url', sha1='sha1')\n", underTest.createOutput().toString()
    }

    @Test
    public void generateRuleWhenOneDoesntAlreadyExists() {
        def dependency = mock(ArtifactDependency)

        when(dependency.name).thenReturn("ruleName")
        when(dependencyCache.externalDependenciesForAllConfigurations()).thenReturn([dependency].toSet())

        def rules = RemoteFileRule.generator(project, dependencyCache)
        assert rules.size() == 1
        assert rules.first().name == "ruleName-mvn"
    }

    @Test
    public void doNotGenerateRuleWhenOneAlreadyExists() {
        def dependency = mock(ArtifactDependency)

        when(dependency.name).thenReturn("ruleName")
        when(dependencyCache.externalDependenciesForAllConfigurations()).thenReturn([dependency].toSet())
        when(dependencyCache.rulesExist("ruleName", "ruleName-mvn")).thenReturn(true)

        assert RemoteFileRule.generator(project, dependencyCache).isEmpty()
    }
}
