package uk.gov.dwp.buckify.dependencies

import groovy.transform.Canonical
import org.gradle.api.artifacts.ResolvedArtifact
import uk.gov.dwp.buckify.BuckifyExtension

@Canonical
class ProjectDependency implements BuckDependency {
    ProjectDependency(ResolvedArtifact artifact, BuckifyExtension.DependencyResolution dependencyResolution) {
        this.ruleName = dependencyResolution.nameResolution artifact
        this.identifier = artifact.owner.identifier
        this.path = dependencyResolution.pathResolution this
    }
}
