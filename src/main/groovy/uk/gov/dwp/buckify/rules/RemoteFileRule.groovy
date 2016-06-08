package uk.gov.dwp.buckify.rules

import groovy.text.SimpleTemplateEngine
import org.gradle.api.Project
import uk.gov.dwp.buckify.BuckifyExtension
import uk.gov.dwp.buckify.dependencies.ArtifactDependency
import uk.gov.dwp.buckify.dependencies.DependencyCache

class RemoteFileRule extends Rule {
    static generator = { Project project, DependencyCache dependencies ->
        def disabled = BuckifyExtension.from(project).disableRemoteFileRules
        dependencies.externalDependenciesForAllConfigurations().collect({ new RemoteFileRule(it, disabled) })
    }

    def out
    def url
    def sha1
    boolean disabled

    RemoteFileRule(ArtifactDependency dependencyTarget, boolean disabled) {
        name = dependencyTarget.ruleName + "-mvn"
        out = dependencyTarget.filename
        url = dependencyTarget.identifier
        sha1 = dependencyTarget.sha1
        this.disabled = disabled
    }

    @Override
    Writable createOutput() {
        new SimpleTemplateEngine().createTemplate(commentIfDisabled() + "remote_file(name='$name', out='$out', url='$url', sha1='$sha1' )\n").make(this.properties)
    }

    private String commentIfDisabled() {
        disabled ? "#" : ""
    }
}
