package uk.gov.dwp.buckify.rules

import org.junit.Test
import uk.gov.dwp.buckify.dependencies.ArtifactDependency

import static org.junit.Assert.assertEquals
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

class RemoteFileRuleTest {

    @Test
    public void createOutput() {
        def dependency = mock(ArtifactDependency)
        when(dependency.ruleName).thenReturn("ruleName")
        when(dependency.filename).thenReturn("filename")
        when(dependency.identifier).thenReturn("url")
        when(dependency.sha1).thenReturn("sha1")

        def underTest = new RemoteFileRule(dependency, false)

        assertEquals "remote_file(name='ruleName-mvn', out='filename', url='url', sha1='sha1')\n", underTest.createOutput().toString()
    }
}
