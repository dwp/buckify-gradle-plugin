package uk.gov.dwp.buckify.rules

import groovy.text.SimpleTemplateEngine
import org.gradle.api.Project
import uk.gov.dwp.buckify.dependencies.ArtifactDependency
import uk.gov.dwp.buckify.dependencies.DependencyCache

class FromNexusRule extends Rule {

    static generator = { Project project, DependencyCache dependencies ->
        dependencies.externalDependenciesForAllConfigurations().unique()
                .findAll({ !dependencies.rulesExist(it.name) })
                .collect({ dep -> new FromNexusRule(dep) })
    }

    private final String version
    private final String group
    private final String artifact
    private final String sha1

    FromNexusRule(ArtifactDependency dependencyTarget) {
        name = dependencyTarget.name
        artifact = dependencyTarget.name
        group = dependencyTarget.group
        version = dependencyTarget.version
        sha1 = dependencyTarget.sha1
    }

    FromNexusRule(String name, String version, String group, String artifact, String sha1) {
        this.name = name
        this.version = version
        this.group = group
        this.artifact = artifact
        this.sha1 = sha1
    }

    @Override
    Writable createOutput() {
        new SimpleTemplateEngine().createTemplate("from_nexus(name='$name', version='$version', ga='$group:$artifact', sha1='$sha1')\n").make(this.properties)
    }
}
