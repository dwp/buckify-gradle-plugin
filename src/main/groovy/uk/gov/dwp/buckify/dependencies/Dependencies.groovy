package uk.gov.dwp.buckify.dependencies

import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.ResolvedArtifact
import org.gradle.internal.component.local.model.DefaultProjectComponentIdentifier
import org.gradle.internal.component.local.model.PublishArtifactLocalArtifactMetaData
import uk.gov.dwp.buckify.BuckifyExtension

class Dependencies {

    Set<ProjectDependency> projectDependencies = []
    Set<ArtifactDependency> declaredExternalDependencies = []
    Set<ArtifactDependency> transitiveDependencies = []
    Set<BuckDependency> configSpecificDependencies = []

    static def factory = { Configuration configuration, BuckifyExtension buckifyExtension, DependencyFactory dependencyFactory->
        def resolvedArtifacts = configuration.resolvedConfiguration.resolvedArtifacts
        def projectArtifacts = findProjectArtifacts(resolvedArtifacts)
        def externalArtifacts = findDeclaredExternalArtifacts(configuration)
        def transitiveArtifacts = (resolvedArtifacts - externalArtifacts) - projectArtifacts
        def configSpecificArtifacts = findConfigSpecificArtifacts(configuration)

        new Dependencies(
                projectArtifacts.collect({
                    dependencyFactory.create(it)
                }).toSet(),
                externalArtifacts.collect({
                    dependencyFactory.create(it)
                }).toSet(),
                transitiveArtifacts.collect({
                    dependencyFactory.create(it)
                }).toSet(),
                configSpecificArtifacts.collect({
                    dependencyFactory.create(it)
                }).toSet()
        )
    }

    Dependencies() {}

    Dependencies(Set<ProjectDependency> projectDependencies,
                 Set<ArtifactDependency> declaredExternalDependencies,
                 Set<ArtifactDependency> transitiveDependencies,
                 Set<BuckDependency> configSpecificDependencies) {
        this.projectDependencies = projectDependencies
        this.declaredExternalDependencies = declaredExternalDependencies
        this.transitiveDependencies = transitiveDependencies
        this.configSpecificDependencies = configSpecificDependencies
    }

    private static HashSet<ResolvedArtifact> findConfigSpecificArtifacts(Configuration configuration) {
        configuration.resolvedConfiguration.firstLevelModuleDependencies
                .collect({ it.moduleArtifacts })
                .flatten()
                .findAll({
            artifact -> configuration.dependencies.matching({ compareDeps(it, artifact) }).size() > 0
        })
    }

    private static boolean compareDeps(Dependency configDep, ResolvedArtifact artifact) {
        "$configDep.group:$configDep.name:$configDep.version" == artifact.owner.identifier.toString() &&
        artifactsHaveSameClassifier(configDep, artifact)
    }

    // differentiates "tests" from "normal" artifacts, for example
    private static boolean artifactsHaveSameClassifier(Dependency configDep, ResolvedArtifact artifact) {
        configDep.artifacts.size() == 0 || configDep.artifacts.first().classifier == artifact.artifact.attributes["classifier"]
    }

    private static HashSet<ResolvedArtifact> findProjectArtifacts(Set<ResolvedArtifact> resolvedArtifacts) {
        resolvedArtifacts.findAll({ isProjectDependency(it) })
    }


    static boolean isProjectDependency(ResolvedArtifact resolvedArtifact) {
        resolvedArtifact.getId().getComponentIdentifier() instanceof DefaultProjectComponentIdentifier
    }

    private static Set<ResolvedArtifact> findDeclaredExternalArtifacts(Configuration configuration) {
        configuration.resolvedConfiguration.firstLevelModuleDependencies
                .collect({ it.moduleArtifacts })
                .flatten()
                .findAll({ !(it.artifactSource.artifact instanceof PublishArtifactLocalArtifactMetaData) })
    }

    public Set<ArtifactDependency> externalDependencies() {
        transitiveDependencies + declaredExternalDependencies
    }

    public Set<BuckDependency> nonTransitiveDependencies() {
        declaredExternalDependencies + projectDependencies
    }
}
