package uk.gov.dwp.buckify.dependencies

import groovy.transform.Canonical
import org.gradle.api.artifacts.ResolvedArtifact

@Canonical
class ProjectDependency implements BuckDependency {
    ProjectDependency(ResolvedArtifact artifact, String projectRuleName) {
        this.name = "//$artifact.name:" + projectRuleName
        this.identifier = artifact.moduleVersion.id
        this.path = name
    }
}
