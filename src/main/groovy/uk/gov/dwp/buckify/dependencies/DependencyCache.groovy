package uk.gov.dwp.buckify.dependencies

import org.gradle.api.Project
import uk.gov.dwp.buckify.BuckifyExtension
import uk.gov.dwp.buckify.rules.PreExistingRules

class DependencyCache {

    Map<String, Dependencies> dependenciesForConfiguration = [:]
    Project project
    Closure<Dependencies> dependenciesFactory
    PreExistingRules preExistingRules

    DependencyCache(Project project, PreExistingRules preExistingRules) {
        this(project, preExistingRules, Dependencies.factory)
    }

    DependencyCache(Project project, PreExistingRules preExistingRules, Closure<Dependencies> dependenciesFactory) {
        this.project = project
        this.preExistingRules = preExistingRules
        this.dependenciesFactory = dependenciesFactory
    }

    Dependencies get(String configurationName) {
        dependenciesForConfiguration.computeIfAbsent(configurationName, {
            def configuration = project.configurations.findByName(configurationName)
            configuration != null ? dependenciesFactory(configuration, BuckifyExtension.from(project), preExistingRules) : new Dependencies()
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
