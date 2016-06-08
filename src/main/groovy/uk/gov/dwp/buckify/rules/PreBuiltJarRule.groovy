package uk.gov.dwp.buckify.rules

import groovy.text.SimpleTemplateEngine
import org.gradle.api.Project
import uk.gov.dwp.buckify.BuckifyExtension
import uk.gov.dwp.buckify.dependencies.DependencyCache

class PreBuiltJarRule extends Rule {
    static generator = { Project project, DependencyCache dependencies ->
        def buckifyExtension = BuckifyExtension.from(project)
        def disabled = buckifyExtension.disablePreBuiltJarRules
        dependencies.externalDependenciesForAllConfigurations().unique().collect({ dep ->
            new PreBuiltJarRule(dep.ruleName, buckifyExtension.binaryJarRuleName(dep.ruleName), disabled)
        })
    }

    private String binaryJar
    private boolean disabled

    PreBuiltJarRule(String name, String binaryJar, boolean disabled) {
        this.name = name
        this.binaryJar = binaryJar
        this.disabled = disabled
    }

    @Override
    Writable createOutput() {
        new SimpleTemplateEngine().createTemplate(commentIfDisabled() + "prebuilt_jar(name='$name', binary_jar='$binaryJar', visibility=${quoteAndSort(visibility)})\n").make(this.properties)
    }

    private String commentIfDisabled() {
        disabled ? "#" : ""
    }
}
