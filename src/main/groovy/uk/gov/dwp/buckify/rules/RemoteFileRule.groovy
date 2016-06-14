package uk.gov.dwp.buckify.rules

import groovy.text.SimpleTemplateEngine
import org.gradle.api.Project
import uk.gov.dwp.buckify.dependencies.ArtifactDependency
import uk.gov.dwp.buckify.dependencies.DependencyCache

class RemoteFileRule extends Rule {
    static generator = { Project project, DependencyCache dependencies ->
        dependencies.externalDependenciesForAllConfigurations()
                .findAll({ !(dependencies.preExistingRules.contains(it.name, it.name + "-mvn")) })
                .collect({ new RemoteFileRule(it) })
    }

    def out
    def url
    def sha1

    RemoteFileRule(ArtifactDependency dependencyTarget) {
        name = dependencyTarget.name + "-mvn"
        out = dependencyTarget.filename
        url = dependencyTarget.identifier
        sha1 = dependencyTarget.sha1
    }

    @Override
    Writable createOutput() {
        new SimpleTemplateEngine().createTemplate("remote_file(name='$name', out='$out', url='$url', sha1='$sha1')\n").make(this.properties)
    }
}
