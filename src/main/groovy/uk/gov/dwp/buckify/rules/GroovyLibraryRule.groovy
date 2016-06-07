package uk.gov.dwp.buckify.rules

import groovy.text.SimpleTemplateEngine
import org.gradle.api.Project
import uk.gov.dwp.buckify.BuckifyExtension
import uk.gov.dwp.buckify.dependencies.Dependencies
import uk.gov.dwp.buckify.dependencies.DependencyCache

class GroovyLibraryRule extends Rule {
    static final sourceDir = "src/main/groovy"
    static final resourcesDir = "src/main/resources"

    static generator = { Project project, DependencyCache dependencies -> BuckifyExtension.from(project).groovyLibrary.predicate ? [new GroovyLibraryRule(project, dependencies)] : [] }

    Dependencies dependencies
    Set<String> resources

    GroovyLibraryRule(Project project, DependencyCache dependencies) {
        this.dependencies = dependencies.compileDependencies()
        this.name = BuckifyExtension.from(project).groovyLibrary.defaultRuleName
        this.resources = project.file(resourcesDir).exists() ? quoted([resourcesDir]) : []
    }

    @Override
    Writable createOutput() {
        new SimpleTemplateEngine().createTemplate("""
groovy_library(
                name="$name",
                srcs=glob(["$sourceDir/**/*.groovy", "$sourceDir/**/*.java"]),
                resources=$resources,
                # transitive deps
                deps=${quoted(dependencies.transitiveDependencies.collect({ it.path }))},
                exported_deps=${quoted(dependencies.nonTransitiveDependencies().collect({ it.path }))},
                visibility=${quoted(visibility)}
)

""").make(this.properties)
    }
}
