package uk.gov.dwp.buckify.rules

import groovy.text.SimpleTemplateEngine
import org.gradle.api.Project
import uk.gov.dwp.buckify.BuckifyExtension
import uk.gov.dwp.buckify.dependencies.Dependencies
import uk.gov.dwp.buckify.dependencies.DependencyCache

class GroovyLibraryRule extends Rule {

    static final sourceDir = "src/main/groovy"
    static final resourcesDir = "src/main/resources"

    static generator = { Project project, DependencyCache dependencies ->
        BuckifyExtension.from(project).groovyLibrary.predicate(project) ? [new GroovyLibraryRule(project, dependencies)] : []
    }

    Dependencies dependencies
    boolean hasResources

    GroovyLibraryRule(Project project, DependencyCache dependencies) {
        this.dependencies = dependencies.compileDependencies()
        this.name = BuckifyExtension.from(project).groovyLibrary.defaultRuleName
        this.hasResources = project.file(resourcesDir).exists()
    }

    @Override
    Writable createOutput() {
        new SimpleTemplateEngine().createTemplate("""
groovy_library(
                name="$name",
                srcs=glob(["$sourceDir/**/*.groovy", "$sourceDir/**/*.java"]),
                resources=${hasResources ? "glob(['$resourcesDir/**/*'])" : ''},
                deps=${quoteAndSort(transitiveDependencyPaths(dependencies))},
                exported_deps=${quoteAndSort(dependencies.nonTransitiveDependencies().collect({ it.path }).toSet())},
                visibility=${quoteAndSort(visibility)}
)

""").make(this.properties)
    }
}
