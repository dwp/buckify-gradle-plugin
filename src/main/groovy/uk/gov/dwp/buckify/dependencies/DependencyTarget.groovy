package uk.gov.dwp.buckify.dependencies

import org.gradle.api.artifacts.ResolvedArtifact

class DependencyTarget {
    ResolvedArtifact artifact
    Closure pathResolution
    private String sha1

    DependencyTarget(ResolvedArtifact artifact, Closure pathResolution) {
        this.artifact = artifact
        this.pathResolution = pathResolution
    }

    String name() {
        artifact.name
    }

    String path() {
        pathResolution artifact
    }

    ResolvedArtifact artifact() {
        artifact
    }

    String sha1() {
        sha1 = sha1 ?: Checksum.generateSHA1(artifact.file)
    }
}
