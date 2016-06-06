package uk.gov.dwp.buckify.rules

import groovy.text.SimpleTemplateEngine
import org.gradle.api.Project
import uk.gov.dwp.buckify.dependencies.DependencyCache
import uk.gov.dwp.buckify.dependencies.DependencyTarget

class RemoteFileRule extends Rule {
    static generator = { Project project, DependencyCache dependencies ->
        dependencies.compileDependencies().externalDependencies().collect({ new RemoteFileRule(it) })
    }

    def out
    def url
    def sha1

    RemoteFileRule(DependencyTarget dependencyTarget) {
        def resolvedArtifact = dependencyTarget.artifact
        name = resolvedArtifact.name + "-mvn"
        out = resolvedArtifact.name + ".jar"
        url = "mvn:${resolvedArtifact.owner.identifier}"
        sha1 = dependencyTarget.sha1()
    }

    @Override
    Writable createOutput() {
        new SimpleTemplateEngine().createTemplate("remote_file(name='$name', out='$out', url='$url', sha1='$sha1' )\n").make(this.properties)
    }
}
