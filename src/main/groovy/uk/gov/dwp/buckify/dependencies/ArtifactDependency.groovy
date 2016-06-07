package uk.gov.dwp.buckify.dependencies

import groovy.transform.Canonical
import org.gradle.api.artifacts.ResolvedArtifact

@Canonical
class ArtifactDependency implements BuckDependency {
    String filename
    String sha1

    ArtifactDependency(ResolvedArtifact artifact, Closure pathResolution) {
        this.ruleName = artifact.artifact.toString()
        this.filename = artifact.file.name
        this.identifier = createIdentifier(artifact)
        this.sha1 = artifact.file.exists() ? Checksum.generateSHA1(artifact.file) : null
        this.path = pathResolution this
    }

    private static String createIdentifier(ResolvedArtifact artifact) {
        def classifier = artifact.artifact.attributes["classifier"]
        artifact.owner.identifier.toString() + (classifier ? ":$classifier" : '')
    }
}
