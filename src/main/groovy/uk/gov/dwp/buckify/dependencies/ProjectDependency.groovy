package uk.gov.dwp.buckify.dependencies

import groovy.transform.Canonical
import org.gradle.api.artifacts.ResolvedArtifact

@Canonical
class ProjectDependency implements BuckDependency {
    ProjectDependency(ResolvedArtifact artifact, Closure dependencyResolution) {
        this.ruleName = artifact.name
        this.identifier = artifact.moduleVersion.id
        this.path = dependencyResolution this
    }
}
