package uk.gov.dwp.buckify.rules

import groovy.text.SimpleTemplateEngine
import org.gradle.api.Project
import org.gradle.api.plugins.GroovyPlugin
import uk.gov.dwp.buckify.BuckifyExtension
import uk.gov.dwp.buckify.dependencies.Dependencies
import uk.gov.dwp.buckify.dependencies.DependencyCache

class GroovyTestRule extends Rule {

    static final sourceDir = "src/test/groovy"
    static final resourcesDir = "src/test/resources"

    // todo - do not create rule when source dir does not contain any java / groovy files
    static generator = { Project project, DependencyCache dependencies ->
        project.plugins.hasPlugin(GroovyPlugin) && project.file(sourceDir).exists() ? [new GroovyTestRule(project, dependencies)] : []
    }

    Dependencies dependencies
    String resources
    boolean autoDeps
    Set<String> sourceUnderTest

    GroovyTestRule(Project project, DependencyCache dependencies) {
        this.dependencies = dependencies.testCompileDependencies()
        this.name = BuckifyExtension.from(project).groovyTestRuleName
        this.sourceUnderTest = [":${BuckifyExtension.from(project).javaLibraryRuleName}"]
        this.autoDeps = BuckifyExtension.from(project).autoDeps
        // todo - check sourceSets property of Java plugin to find actual resources dir
        this.resources = project.file(resourcesDir).exists() ? "glob(['$resourcesDir/**/*'])" : '[]'
    }

    @Override
    Writable createOutput() {
        new SimpleTemplateEngine().createTemplate("""
groovy_test(
                name="$name",
                srcs=glob(["src/main/groovy/**/*.groovy", "src/main/groovy/**/*.java"]),
                resources=$resources,
                ${formatted(dependencies)}
                visibility=${quoteAndSort(visibility)}
)

""").make(this.properties)
    }
}
