package uk.gov.dwp.buckify

import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test
import uk.gov.dwp.buckify.dependencies.DependencyCache
import uk.gov.dwp.buckify.rules.JavaLibraryRule

class JavaLibraryRuleGeneratorTest {
     final Project project = ProjectBuilder.builder().withProjectDir(new File("src/test/resources/dummy-java-groovy-project")).build()

    @Test
    public void createJavaLibraryRuleWhenJavaPluginAndSourceDirectoriesExist() {
        project.extensions.create("buckify", BuckifyExtension, "console")
        project.plugins.apply(JavaPlugin)

        def rules = JavaLibraryRule.generator(project, new DependencyCache(project))
        assert rules.size() == 1
        assert rules.first() instanceof JavaLibraryRule
    }

    @Test
    public void doNotCreateJavaLibraryRuleWhenJavaPluginDoesNotExist() {
        def rules = JavaLibraryRule.generator(project, new DependencyCache(project))
        assert rules.isEmpty()
    }
}