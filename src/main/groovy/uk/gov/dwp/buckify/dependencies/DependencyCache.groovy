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
            new Dependencies(project.configurations.findByName(configurationName), BuckifyExtension.from(project))
        })
    }

    Dependencies compileDependencies() {
        get("compile")
    }

    Dependencies testCompileDependencies() {
        get("testCompile")
    }

    Set<ArtifactDependency> externalDependenciesForAllConfigurations() {
        testCompileDependencies().externalDependencies() + compileDependencies().externalDependencies()
    }
}
