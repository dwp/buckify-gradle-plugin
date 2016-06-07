package uk.gov.dwp.buckify.dependencies

import groovy.transform.Canonical
import org.gradle.api.artifacts.ResolvedArtifact

@Canonical
class ProjectDependency implements Dependency {
    ProjectDependency(ResolvedArtifact artifact, Closure pathResolution) {
        this.name = artifact.name
        this.rulePath = pathResolution artifact
        this.identifier = artifact.owner.identifier
    }
}
