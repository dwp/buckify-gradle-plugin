package uk.gov.dwp.buckify.dependencies

import org.gradle.api.artifacts.ResolvedArtifact
import org.gradle.api.artifacts.component.ComponentArtifactIdentifier
import org.gradle.api.internal.artifacts.DefaultModuleVersionIdentifier
import org.gradle.api.internal.artifacts.ivyservice.dynamicversions.DefaultResolvedModuleVersion
import org.gradle.internal.component.external.model.DefaultModuleComponentIdentifier
import org.gradle.internal.component.local.model.DefaultProjectComponentIdentifier
import org.junit.Before
import org.junit.Test
import uk.gov.dwp.buckify.BuckifyExtension
import uk.gov.dwp.buckify.rules.PreExistingRules

import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

class DependencyFactoryTest {

    private preExistingRules = mock(PreExistingRules)
    private buckifyExtension = mock(BuckifyExtension)
    private underTest = new DependencyFactory(this.preExistingRules, buckifyExtension)
    private ResolvedArtifact externalArtifact = externalArtifact(group: "externalGroup",
                                                                 name: "name",
                                                                 type: "type",
                                                                 filename: "filename",
                                                                 version: "version")

    private ResolvedArtifact projectArtifact = projectArtifact(group: "projectGroup",
                                                               name: "name",
                                                               projectPath: ":projectPath",
                                                               type: "type",
                                                               filename: "filename",
                                                               version: "version")

    @Before
    public void setUp() {
        when(buckifyExtension.nomenclature).thenReturn({ ResolvedArtifact artifact -> artifact.name })
        when(buckifyExtension.javaLibraryRuleName).thenReturn("java")
    }

    @Test
    public void createArtifactDependency() {
        when(preExistingRules.resolvePath("name")).thenReturn(":rule-path")

        def dependency = underTest.create(externalArtifact)

        assert dependency instanceof ArtifactDependency
        assert dependency.name == "name"
        assert dependency.path == ":rule-path"
        assert dependency.filename == "filename"
        // file does not exist
        assert dependency.sha1 == null
        assert dependency.mavenIdentifier == "mvn:externalGroup:name:type:version"
    }

    @Test
    public void createProjectDependency() {
        when(preExistingRules.contains("name")).thenReturn(false)

        def dependency = underTest.create(projectArtifact)

        assert dependency instanceof ProjectDependency
        assert dependency.name == "name"
        assert dependency.path == "//projectPath:java"
        assert dependency.identifier == "projectGroup:name:version"
    }

    @Test
    public void mavenIdentifierWithClassifier() {
        when(this.externalArtifact.getClassifier()).thenReturn("classifier")
        def identifier = DependencyFactory.createMavenIdentifier(externalArtifact)

        assert identifier == "mvn:externalGroup:name:type:classifier:version"
    }

    private static ResolvedArtifact artifact(Map<String, String> args) {
        def artifact = mock(ResolvedArtifact)
        when(artifact.getModuleVersion()).thenReturn(new DefaultResolvedModuleVersion(new DefaultModuleVersionIdentifier(args.group, args.name, args.version)))
        when(artifact.getType()).thenReturn(args.type)
        when(artifact.file).thenReturn(new File(args.filename))
        when(artifact.name).thenReturn(args.name)
        artifact
    }

    private static ResolvedArtifact externalArtifact(Map<String, String> args) {
        def artifact = artifact(args)
        def identifier = mock(ComponentArtifactIdentifier)
        when(artifact.getId()).thenReturn(identifier)
        when(identifier.getComponentIdentifier()).thenReturn(new DefaultModuleComponentIdentifier(args.group, "", args.version))
        artifact
    }

    private static ResolvedArtifact projectArtifact(Map<String, String> args) {
        def artifact = artifact(args)
        def identifier = mock(ComponentArtifactIdentifier)
        when(artifact.getId()).thenReturn(identifier)
        when(identifier.getComponentIdentifier()).thenReturn(new DefaultProjectComponentIdentifier(args.projectPath))
        artifact
    }

}
