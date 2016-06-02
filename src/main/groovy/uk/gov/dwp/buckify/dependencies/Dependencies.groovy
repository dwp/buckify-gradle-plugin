package uk.gov.dwp.buckify.dependencies

import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ExternalDependency
import org.gradle.api.artifacts.ProjectDependency
import uk.gov.dwp.buckify.BuckifyExtension

class Dependencies {
    Set<ExternalDependency> externalDependencies
    Set<ProjectDependency> projectDependencies
    private BuckifyExtension buckifyExtension

    Dependencies(Configuration configuration, BuckifyExtension buckifyExtension) {
        this.buckifyExtension = buckifyExtension
        this.externalDependencies = configuration.allDependencies.findAll({ it instanceof ExternalDependency })
        this.projectDependencies = configuration.allDependencies.findAll({ it instanceof ProjectDependency })
    }

    public Set<String> allDependencyNames() {
        externalDependencyNames() + projectDependencyNames()
    }

    public Set<String> externalDependencyNames() {
        externalDependencies.collect { it.name }
    }

    public Set<String> projectDependencyNames() {
        projectDependencies.collect { it.name }
    }

    public Set<String> allDependencyPaths() {
        externalDependencyPaths() + projectDependencyPaths()
    }

    public Set<String> externalDependencyPaths() {
        externalDependencies.collect { buckifyExtension.externalDependencyRuleResolution it }
    }

    public Set<String> projectDependencyPaths() {
        projectDependencies.collect { buckifyExtension.projectDependencyRuleResolution it }
    }

    static Dependencies compileDependencies(Project project) {
        new Dependencies(project.configurations.findByName("compile"), BuckifyExtension.from(project))
    }

    static Dependencies testCompileDependencies(Project project) {
        new Dependencies(project.configurations.findByName("testCompile"), BuckifyExtension.from(project))
    }
}
