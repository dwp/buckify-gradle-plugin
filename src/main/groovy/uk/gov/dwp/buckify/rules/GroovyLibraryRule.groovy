package uk.gov.dwp.buckify.rules

import groovy.text.SimpleTemplateEngine
import org.gradle.api.Project
import uk.gov.dwp.buckify.BuckifyExtension
import uk.gov.dwp.buckify.dependencies.Dependencies

import static uk.gov.dwp.buckify.dependencies.Dependencies.compileDependencies

class GroovyLibraryRule extends Rule {
    static final sourceDir = "src/main/groovy"
    static final resourcesDir = "src/main/resources"

    static generator = { Project project -> BuckifyExtension.from(project).groovyLibrary.predicate ? [new GroovyLibraryRule(project)] : [] }

    Dependencies dependencies
    Set<String> resources

    GroovyLibraryRule(Project project) {
        this.dependencies = compileDependencies(project)
        this.name = BuckifyExtension.from(project).groovyLibrary.defaultRuleName
        this.resources = project.file(resourcesDir).exists() ? quoted([resourcesDir]) : []
    }

    @Override
    Writable createOutput() {
        new SimpleTemplateEngine().createTemplate("""groovy_library(
                name="$name",
                srcs=glob(["$sourceDir/**/*.groovy", "$sourceDir/**/*.java"]),
                resources=$resources,
                deps=[],
                exported_deps=${quoted(dependencies.allDependencyPaths())},
                visibility=${quoted(visibility)}
)

""").make(this.properties)
    }
}
