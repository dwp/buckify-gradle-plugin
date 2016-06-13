package uk.gov.dwp.buckify.dependencies

import groovy.transform.Canonical
import org.gradle.api.artifacts.ResolvedArtifact
import uk.gov.dwp.buckify.rules.PreExistingRules

@Canonical
class ArtifactDependency implements BuckDependency {

    String filename
    String sha1

    ArtifactDependency(ResolvedArtifact artifact, PreExistingRules preExistingRules) {
        this.name = artifact.name + (artifact.classifier ? "-$artifact.classifier" : "")
        this.filename = artifact.file.name
        this.identifier = createMavenIdentifier(artifact)
        this.sha1 = artifact.file.exists() ? Checksum.generateSHA1(artifact.file) : null
        this.path = preExistingRules.contains(name) ? preExistingRules.findPath(name) : name
    }

    // mvn:optionalServer:group:id:type:classifier:version
    static String createMavenIdentifier(ResolvedArtifact artifact) {
        def id = artifact.getModuleVersion().getId()
        "mvn:${id.group}:${id.name}:${artifact.type}:${artifact.classifier ? "${artifact.classifier}:" : ""}${id.version}"
    }
}
