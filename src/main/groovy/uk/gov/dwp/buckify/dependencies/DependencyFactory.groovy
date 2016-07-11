package uk.gov.dwp.buckify.dependencies

import org.gradle.api.artifacts.ResolvedArtifact
import uk.gov.dwp.buckify.BuckifyExtension
import uk.gov.dwp.buckify.rules.PreExistingRules

class DependencyFactory {

    PreExistingRules preExistingRules
    BuckifyExtension buckifyExtension
    Closure<String> checksum

    DependencyFactory(PreExistingRules preExistingRules,
                      BuckifyExtension buckifyExtension) {
        this(preExistingRules, buckifyExtension, { file -> Checksum.generateSHA1(file) })
    }

    DependencyFactory(PreExistingRules preExistingRules,
                      BuckifyExtension buckifyExtension,
                      Closure<String> checksum) {
        this.preExistingRules = preExistingRules
        this.checksum = checksum
        this.buckifyExtension = buckifyExtension
    }

    def create(ResolvedArtifact artifact) {
        if (Dependencies.isProjectDependency(artifact)) {
            new ProjectDependency(artifact.name, projectPath(artifact), artifact.moduleVersion.id)
        } else {
            def name = buckifyExtension.nomenclature(artifact)
            new ArtifactDependency(name,
                                   artifact.moduleVersion.id.group,
                                   artifact.moduleVersion.id.version,
                                   createExternalPath(name),
                                   createMavenIdentifier(artifact),
                                   artifact.file.name,
                                   createChecksum(artifact)
            )
        }
    }

    private String projectPath(ResolvedArtifact artifact) {
        "${artifact.id.componentIdentifier.displayName.replace("project :", "//").replace(':', "/")}:${buckifyExtension.javaLibraryRuleName}"
    }

    private String createChecksum(ResolvedArtifact artifact) {
        artifact.file.exists() ? checksum(artifact.file) : null
    }

    private String createExternalPath(String name) {
        preExistingRules.resolvePath(name)
    }

    // mvn:optionalServer:group:id:type:classifier:version
    static String createMavenIdentifier(ResolvedArtifact artifact) {
        def id = artifact.getModuleVersion().getId()
        "mvn:${id.group}:${id.name}:${artifact.type}:${artifact.classifier ? "${artifact.classifier}:" : ""}${id.version}"
    }
}
