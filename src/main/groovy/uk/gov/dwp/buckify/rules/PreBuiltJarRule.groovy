package uk.gov.dwp.buckify.rules

import groovy.text.SimpleTemplateEngine
import org.gradle.api.Project
import uk.gov.dwp.buckify.BuckifyExtension
import uk.gov.dwp.buckify.dependencies.DependencyCache

class PreBuiltJarRule extends Rule {

    static generator = { Project project, DependencyCache dependencies ->
        def buckifyExtension = BuckifyExtension.from(project)
        dependencies.externalDependenciesForAllConfigurations().unique()
                .findAll({ !dependencies.rulesExist(it.name) })
                .collect({ dep -> new PreBuiltJarRule(dep.name, buckifyExtension.binaryJarRuleName(dep.name)) })
    }

    private String binaryJar

    PreBuiltJarRule(String name, String binaryJar) {
        this.name = name
        this.binaryJar = binaryJar
    }

    @Override
    Writable createOutput() {
        new SimpleTemplateEngine().createTemplate("prebuilt_jar(name='$name', binary_jar='$binaryJar', visibility=${quoteAndSort(visibility)})\n").make(this.properties)
    }
}
