package uk.gov.dwp.buckify.dependencies

import org.gradle.api.artifacts.ResolvedArtifact
import org.gradle.api.internal.artifacts.DefaultModuleVersionIdentifier
import org.gradle.api.internal.artifacts.ivyservice.dynamicversions.DefaultResolvedModuleVersion
import org.junit.Test

import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

class ArtifactDependencyTest {
    @Test
    public void mavenIdentifierWithNoClassifier() {
        ResolvedArtifact artifact = artifact(group: "group", name: "name", type: "type", version: "version")
        def identifier = ArtifactDependency.createMavenIdentifier(artifact)

        assert identifier == "mvn:group:name:type:version"
    }

    @Test
    public void mavenIdentifierWithClassifier() {
        ResolvedArtifact artifact = artifact(group: "group", name: "name", type: "type", version: "version")
        when(artifact.getClassifier()).thenReturn("classifier")
        def identifier = ArtifactDependency.createMavenIdentifier(artifact)

        assert identifier == "mvn:group:name:type:classifier:version"
    }

    private static ResolvedArtifact artifact(Map<String, String> args) {
        def artifact = mock(ResolvedArtifact)
        when(artifact.getModuleVersion()).thenReturn(new DefaultResolvedModuleVersion(new DefaultModuleVersionIdentifier(args.group, args.name, args.version)))
        when(artifact.getType()).thenReturn(args.type)
        artifact
    }
}
