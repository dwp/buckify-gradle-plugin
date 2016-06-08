package uk.gov.dwp.buckify.rules

import groovy.text.SimpleTemplateEngine
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import uk.gov.dwp.buckify.BuckifyExtension
import uk.gov.dwp.buckify.dependencies.Dependencies
import uk.gov.dwp.buckify.dependencies.DependencyCache

class JavaLibraryRule extends Rule {
    static final sourceDir = "src/main/java"
    static final resourcesDir = "src/main/resources"

    static generator = { Project project, DependencyCache dependencies -> project.plugins.hasPlugin(JavaPlugin) && project.file(sourceDir).exists() ? [new JavaLibraryRule(project, dependencies)] : [] }

    Dependencies dependencies
    boolean autoDeps
    boolean hasResources

    JavaLibraryRule(Project project, DependencyCache dependencies) {
        def buckifyExtension = project.extensions.findByType(BuckifyExtension)
        this.dependencies = dependencies.compileDependencies()
        this.name = BuckifyExtension.from(project).javaLibrary.defaultRuleName
        this.autoDeps = buckifyExtension.autoDeps
        // todo - check sourceSets property of Java plugin to find actual resources dir
        this.hasResources = project.file(resourcesDir).exists()
    }

    @Override
    Writable createOutput() {
        new SimpleTemplateEngine().createTemplate("""
java_library(
                name="$name",
                autodeps=${toPythonBoolean(autoDeps)},
                srcs=glob(["$sourceDir/**/*.java"]),
                resources=${hasResources ? "glob(['$resourcesDir/**/*'])" : ''},
                resources_root=${hasResources ? "'$resourcesDir'" : ''},
                deps=${quoteAndSort(transitiveDependencyPaths(dependencies))},
                exported_deps=${quoteAndSort(dependencies.nonTransitiveDependencies().collect({ it.path }))},
                visibility=${quoteAndSort(visibility)}
)

""").make(this.properties)
    }
}
