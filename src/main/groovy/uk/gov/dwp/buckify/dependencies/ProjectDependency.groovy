package uk.gov.dwp.buckify.dependencies

import groovy.transform.Canonical
import org.gradle.api.artifacts.ModuleVersionIdentifier

@Canonical
class ProjectDependency implements BuckDependency {
    ProjectDependency(String name, String path, ModuleVersionIdentifier identifier) {
        this.name = name
        this.identifier = identifier
        this.path = path
    }
}
