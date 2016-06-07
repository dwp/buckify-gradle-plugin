package uk.gov.dwp.buckify.dependencies

import groovy.transform.Canonical
import org.gradle.api.artifacts.ResolvedArtifact

@Canonical
class ProjectDependency implements BuckDependency {
    ProjectDependency(ResolvedArtifact artifact, Closure pathResolution) {
        this.ruleName = artifact.name
        this.identifier = artifact.owner.identifier
        this.path = pathResolution this
    }
}
