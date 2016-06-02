package uk.gov.dwp.buckify.rules

import groovy.text.SimpleTemplateEngine
import org.gradle.api.Project
import uk.gov.dwp.buckify.BuckifyExtension

import static uk.gov.dwp.buckify.dependencies.Dependencies.compileDependencies

class PreBuiltJarRule extends Rule {
    static generator = { Project project -> compileDependencies(project).externalDependencyNames().collect({ new PreBuiltJarRule(it, BuckifyExtension.from(project).binaryJarResolution(it)) }) }

    private String binaryJar

    PreBuiltJarRule(String name, String binaryJar) {
        this.name = name
        this.binaryJar = binaryJar
    }

    @Override
    Writable createOutput() {
        new SimpleTemplateEngine().createTemplate("prebuilt_jar(name='$name', binary_jar='$binaryJar', visibility=${quoted(visibility)})\n").make(this.properties)
    }
}
