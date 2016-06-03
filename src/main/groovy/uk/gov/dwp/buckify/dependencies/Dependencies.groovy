package uk.gov.dwp.buckify.dependencies

import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ExternalDependency
import org.gradle.api.artifacts.LenientConfiguration
import org.gradle.api.artifacts.ProjectDependency
import org.gradle.api.artifacts.ResolvedArtifact
import org.gradle.api.internal.artifacts.ivyservice.DefaultLenientConfiguration
import org.gradle.api.internal.artifacts.ivyservice.ErrorHandlingArtifactDependencyResolver
import uk.gov.dwp.buckify.BuckifyExtension

class Dependencies {
    Set<ExternalDependency> externalDependencies
    Set<ProjectDependency> projectDependencies
    Set<ResolvedArtifact> resolvedArtifacts;
    private BuckifyExtension buckifyExtension

    Dependencies(Configuration configuration, BuckifyExtension buckifyExtension) {
        this.buckifyExtension = buckifyExtension
        this.externalDependencies = configuration.allDependencies.findAll({ it instanceof ExternalDependency })
        this.resolvedArtifacts = resolve(configuration)
        this.projectDependencies = configuration.allDependencies.findAll({ it instanceof ProjectDependency })
    }

    Set<String> resolve(Configuration compileConfiguration) {

        def resolvedConfiguration = compileConfiguration.resolvedConfiguration
//        ErrorHandlingArtifactDependencyResolver.ErrorHandlingLenientConfiguration lanientConfiguration = resolvedConfiguration.lenientConfiguration
//        lanientConfiguration.metaClass.rethrowFailure = {return }
//        lanientConfiguration.metaClass.hasError = {return false}


       resolvedConfiguration.getFirstLevelModuleDependencies()
        def resolvedArtifacts = resolvedConfiguration.resolvedArtifacts
        resolvedArtifacts
    }

    public Set<String> allDependencyNames() {
        externalDependencyNames() + projectDependencyNames()
    }

    public Set<String> externalDependencyNames() {
        resolvedArtifacts.collect{dep -> dep.owner.identifier.name}
    }

    public Set<String> projectDependencyNames() {
        projectDependencies.collect { it.name }
    }

    public Set<String> allDependencyPaths() {
        externalDependencyPaths() + projectDependencyPaths()
    }

    public Set<String> externalDependencyPaths() {
        resolvedArtifacts.collect { buckifyExtension.resolvedExternalDependencyRuleResolution it }
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
