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
        BuckifyExtension.from(project).groovyLibraryPredicate(project) ? [new GroovyLibraryRule(project, dependencies)] : []
    }

    Dependencies dependencies
    String resources

    GroovyLibraryRule(Project project, DependencyCache dependencies) {
        this.dependencies = dependencies.compileDependencies()
        this.name = BuckifyExtension.from(project).groovyLibraryRuleName
        this.resources = project.file(resourcesDir).exists() ? "glob(['$resourcesDir/**/*'])" : '[]'
    }

    @Override
    Writable createOutput() {
        new SimpleTemplateEngine().createTemplate("""
groovy_library(
                name="$name",
                srcs=glob(["$sourceDir/**/*.groovy", "$sourceDir/**/*.java"]),
                resources=$resources,
                ${deps()}
                visibility=${quoteAndSort(visibility)}
)

""").make(this.properties)
    }

    private String deps() {
        def nonTransitiveDeps = pathsTo(dependencies.nonTransitiveDependencies()).collect({ "$it," }).join("\n")
        def transitiveDeps = pathsTo(dependencies.transitiveDependencies).join(',\n')
        """deps=[
$nonTransitiveDeps
                    #transitive deps
$transitiveDeps
                ],"""
    }
}
