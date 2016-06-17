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

    static generator = { Project project, DependencyCache dependencies ->
        project.plugins.hasPlugin(JavaPlugin) && project.file(sourceDir).exists() ? [new JavaLibraryRule(project, dependencies)] : []
    }

    Dependencies dependencies
    boolean autoDeps
    boolean hasResources

    JavaLibraryRule(Project project, DependencyCache dependencies) {
        def buckifyExtension = BuckifyExtension.from(project)
        this.dependencies = dependencies.compileDependencies()
        this.name = buckifyExtension.javaLibraryRuleName
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
                ${resources()}
                ${deps()}
                visibility=${quoteAndSort(visibility)}
)

""").make(this.properties)
    }

    private String resources() {
        hasResources ? "resources=glob(['$resourcesDir/**/*']),\nresources_root='$resourcesDir'," : '# no resources found'
    }

    private String deps() {
        def nonTransitiveDeps = pathsTo(dependencies.nonTransitiveDependencies())
        def transitiveDeps = pathsTo(dependencies.transitiveDependencies)
        """deps=[
${nonTransitiveDeps.collect({ "$it," }).join("\n")}
                    #transitive deps
${transitiveDeps.join(',\n')}
                ],"""
    }
}
