package uk.gov.dwp.buckify.dependencies

import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.ResolvedArtifact
import org.gradle.api.artifacts.ResolvedDependency
import org.gradle.internal.component.local.model.PublishArtifactLocalArtifactMetaData
import uk.gov.dwp.buckify.BuckifyExtension

class Dependencies {
    Set<DependencyTarget> projectDependencies
    Set<DependencyTarget> declaredExternalDependencies
    Set<DependencyTarget> transitiveDependencies
    Set<DependencyTarget> configSpecificDependencies

    Dependencies(Configuration configuration, BuckifyExtension buckifyExtension) {
        def resolvedArtifacts = configuration.resolvedConfiguration.resolvedArtifacts
        def projectArtifacts = findProjectArtifacts(resolvedArtifacts)
        def externalArtifacts = findDeclaredExternalArtifacts(configuration)
        def transitiveArtifacts = (resolvedArtifacts - externalArtifacts) - projectArtifacts
        def configSpecificArtifacts = findArtifacts(findConfigSpecificDependencies(configuration))

        configSpecificDependencies = configSpecificArtifacts.collect({ new DependencyTarget(it, buckifyExtension.externalDependencyRuleResolution)} )
        projectDependencies = projectArtifacts.collect({ new DependencyTarget(it, buckifyExtension.projectDependencyRuleResolution) })
        declaredExternalDependencies = externalArtifacts.collect({ new DependencyTarget(it, buckifyExtension.externalDependencyRuleResolution) })
        transitiveDependencies = transitiveArtifacts.collect({ new DependencyTarget(it, buckifyExtension.externalDependencyRuleResolution) })
    }

    private static HashSet<ResolvedDependency> findConfigSpecificDependencies(Configuration configuration) {
        configuration.resolvedConfiguration.firstLevelModuleDependencies.findAll({ moduleDep ->
            configuration.dependencies.matching({ compareDeps(it, moduleDep) }).size() > 0
        })
    }

    private static boolean compareDeps(Dependency it, ResolvedDependency moduleDep) {
        "$it.group:$it.name:$it.version" == moduleDep.name
    }

    private static HashSet<ResolvedArtifact> findProjectArtifacts(Set<ResolvedArtifact> resolvedArtifacts) {
        resolvedArtifacts.findAll({
            it.artifactSource.artifact instanceof PublishArtifactLocalArtifactMetaData
        })
    }

    private static Set<ResolvedArtifact> findDeclaredExternalArtifacts(Configuration configuration) {
        def firstLevelModuleDependencies = configuration.resolvedConfiguration.firstLevelModuleDependencies
        findArtifacts(firstLevelModuleDependencies)
                .findAll({ !(it.artifactSource.artifact instanceof PublishArtifactLocalArtifactMetaData) })
    }

    private static ArrayList<ResolvedArtifact> findArtifacts(Set<ResolvedDependency> firstLevelModuleDependencies) {
        firstLevelModuleDependencies
                .collect({ it.moduleArtifacts })
                .flatten()
    }

    public Set<DependencyTarget> allDependencies() {
        externalDependencies() + projectDependencies()
    }

    public Set<DependencyTarget> externalDependencies() {
        transitiveDependencies() + declaredDependencies()
    }

    public Set<DependencyTarget> declaredDependencies() {
        declaredExternalDependencies
    }

    public Set<DependencyTarget> transitiveDependencies() {
        transitiveDependencies
    }

    public Set<DependencyTarget> nonTransitiveDependencies() {
        declaredDependencies() + projectDependencies()
    }

    public Set<DependencyTarget> projectDependencies() {
        projectDependencies
    }
}
