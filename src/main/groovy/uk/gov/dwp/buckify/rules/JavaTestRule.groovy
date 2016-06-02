package uk.gov.dwp.buckify.rules

import groovy.text.SimpleTemplateEngine
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import uk.gov.dwp.buckify.BuckifyExtension
import uk.gov.dwp.buckify.dependencies.Dependencies

import static uk.gov.dwp.buckify.dependencies.Dependencies.testCompileDependencies

class JavaTestRule extends Rule {
    static final sourceDir = "src/test/java"
    static final resourcesDir = "src/test/resources"

    static generator = { Project project -> project.plugins.hasPlugin(JavaPlugin) && project.file(sourceDir).exists() ? [new JavaTestRule(project)] : [] }

    Dependencies dependencies
    Set<String> resources
    boolean autoDeps
    Set<String> sourceUnderTest

    JavaTestRule(Project project) {
        this.dependencies = testCompileDependencies(project)
        this.name = BuckifyExtension.from(project).javaTestLibrary.defaultRuleName
        this.sourceUnderTest = [project.name + "-java"]
        this.autoDeps = BuckifyExtension.from(project).autoDeps
        this.resources = project.file(resourcesDir).exists() ? quoted([resourcesDir]) : []
    }

    @Override
    Writable createOutput() {
        new SimpleTemplateEngine().createTemplate("""java_test(
                name="$name",
                autodeps=${toPythonBoolean(autoDeps)},
                source_under_test=${quoted(sourceUnderTest)},
                srcs=glob(["$sourceDir/**/*.java"]),
                resources=$resources,
                deps=${quoted(dependencies.allDependencyPaths())},
                visibility=${quoted(visibility)}
)

""").make(this.properties)
    }
}
