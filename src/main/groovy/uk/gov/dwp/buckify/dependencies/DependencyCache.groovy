package uk.gov.dwp.buckify.dependencies

import org.gradle.api.Project
import uk.gov.dwp.buckify.BuckifyExtension

class DependencyCache {
    Map<String, Dependencies> dependenciesForConfiguration = [:]
    Project project

    DependencyCache(Project project) {
        this.project = project
    }

    Dependencies get(String configurationName) {
        dependenciesForConfiguration.computeIfAbsent(configurationName, {
            def configuration = project.configurations.findByName(configurationName)
            configuration != null ? new Dependencies(configuration, BuckifyExtension.from(project)) : new Dependencies()
        })
    }

    Dependencies compileDependencies() {
        get("compile")
    }

    Dependencies testCompileDependencies() {
        get("testCompile")
    }

    Set<ArtifactDependency> externalDependenciesForAllConfigurations() {
        (testCompileDependencies().externalDependencies() + compileDependencies().externalDependencies()).sort()
    }
}
