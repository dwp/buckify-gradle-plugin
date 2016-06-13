package uk.gov.dwp.buckify.dependencies

import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.ResolvedArtifact
import org.gradle.internal.component.local.model.PublishArtifactLocalArtifactMetaData
import uk.gov.dwp.buckify.BuckifyExtension
import uk.gov.dwp.buckify.rules.PreExistingRules

class Dependencies {

    Set<ProjectDependency> projectDependencies = []
    Set<ArtifactDependency> declaredExternalDependencies = []
    Set<ArtifactDependency> transitiveDependencies = []
    Set<ArtifactDependency> configSpecificDependencies = []

    static def factory = { Configuration configuration, BuckifyExtension buckifyExtension, PreExistingRules preExistingRules ->
        def resolvedArtifacts = configuration.resolvedConfiguration.resolvedArtifacts
        def projectArtifacts = findProjectArtifacts(resolvedArtifacts)
        def externalArtifacts = findDeclaredExternalArtifacts(configuration)
        def transitiveArtifacts = (resolvedArtifacts - externalArtifacts) - projectArtifacts
        def configSpecificArtifacts = findConfigSpecificArtifacts(configuration)

        new Dependencies(
                projectArtifacts.collect({
                    new ProjectDependency(it, buckifyExtension.javaLibraryRuleName)
                }).toSet(),
                externalArtifacts.collect({
                    new ArtifactDependency(it, preExistingRules)
                }).toSet(),
                transitiveArtifacts.collect({
                    new ArtifactDependency(it, preExistingRules)
                }).toSet(),
                configSpecificArtifacts.collect({
                    new ArtifactDependency(it, preExistingRules)
                }).toSet()
        )
    }

    Dependencies() {}

    Dependencies(Set<ProjectDependency> projectDependencies,
                 Set<ArtifactDependency> declaredExternalDependencies,
                 Set<ArtifactDependency> transitiveDependencies,
                 Set<ArtifactDependency> configSpecificDependencies) {
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
        resolvedArtifacts.findAll({
            it.artifactSource.artifact instanceof PublishArtifactLocalArtifactMetaData
        })
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
