package uk.gov.dwp.buckify.rules

import groovy.text.SimpleTemplateEngine
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import uk.gov.dwp.buckify.BuckifyExtension
import uk.gov.dwp.buckify.dependencies.Dependencies
import uk.gov.dwp.buckify.dependencies.DependencyCache

class JavaTestRule extends Rule {
    static final sourceDir = "src/test/java"
    static final resourcesDir = "src/test/resources"

    static generator = { Project project, DependencyCache dependencies -> project.plugins.hasPlugin(JavaPlugin) && project.file(sourceDir).exists() ? [new JavaTestRule(project, dependencies)] : [] }

    Dependencies dependencies
    Set<String> resources
    boolean autoDeps
    Set<String> sourceUnderTest

    JavaTestRule(Project project, DependencyCache dependencies) {
        this.dependencies = dependencies.testCompileDependencies()
        this.name = BuckifyExtension.from(project).javaTestLibrary.defaultRuleName
        this.sourceUnderTest = [BuckifyExtension.from(project).javaLibrary.defaultRuleName]
        this.autoDeps = BuckifyExtension.from(project).autoDeps
        this.resources = project.file(resourcesDir).exists() ? quoted([resourcesDir]) : []
    }

    @Override
    Writable createOutput() {
        new SimpleTemplateEngine().createTemplate("""
java_test(
                name="$name",
                autodeps=${toPythonBoolean(autoDeps)},
                source_under_test=${quoted(sourceUnderTest)},
                srcs=glob(["$sourceDir/**/*.java"]),
                resources=$resources,
                deps=${quoted(dependencies.configSpecificDependencies.collect({ it.path }))},
                visibility=${quoted(visibility)}
)

""").make(this.properties)
    }
}
