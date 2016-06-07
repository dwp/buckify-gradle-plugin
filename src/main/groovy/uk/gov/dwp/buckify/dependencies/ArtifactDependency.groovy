package uk.gov.dwp.buckify.dependencies

import groovy.transform.Canonical
import org.gradle.api.artifacts.ResolvedArtifact

@Canonical
class ArtifactDependency implements Dependency{
    String filename
    String sha1

    ArtifactDependency(ResolvedArtifact artifact, Closure pathResolution) {
        this.name = artifact.artifact.toString()
        this.filename = artifact.file.name
        this.rulePath = pathResolution artifact
        this.identifier = createIdentifier(artifact)
        this.sha1 = artifact.file.exists() ? Checksum.generateSHA1(artifact.file) : null
    }

    private String createIdentifier(ResolvedArtifact artifact) {
        def classifier = artifact.artifact.attributes["classifier"]
        artifact.owner.identifier.toString() + (classifier ? ":$classifier" : '')
    }
}
