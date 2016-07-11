package uk.gov.dwp.buckify.dependencies

import groovy.transform.Canonical
import org.gradle.api.artifacts.ModuleVersionIdentifier

@Canonical
class ProjectDependency implements BuckDependency {
    final identifier

    ProjectDependency(String name, String path, ModuleVersionIdentifier identifier) {
        this.name = name
        this.identifier = identifier.toString()
        this.path = path
    }
}
