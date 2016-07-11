package uk.gov.dwp.buckify.rules

import org.gradle.api.Project
import org.gradle.api.plugins.GroovyPlugin
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import uk.gov.dwp.buckify.BuckifyExtension
import uk.gov.dwp.buckify.dependencies.DependencyCache

class GroovyTestLibraryRuleGeneratorTest {
     final Project project = ProjectBuilder.builder().build()

    @Before
    public void setUp() {
        project.file("src/test/groovy").mkdirs()
    }

    @Ignore
    @Test
    public void createRuleWhenGroovyPluginAndSourceDirectoriesExist() {
        project.file("src/test/groovy/Dummy.groovy").createNewFile()

        project.extensions.create("buckify", BuckifyExtension)
        project.plugins.apply(GroovyPlugin)

        def rules = GroovyTestRule.generator(project, new DependencyCache(project, new PreExistingRules()))
        assert rules.size() == 1
        assert rules.first() instanceof GroovyTestRule
    }

    @Ignore
    @Test
    public void doNotCreateRuleWhenSourceDirectoriesContainNoClasses() {
        project.extensions.create("buckify", BuckifyExtension)
        project.plugins.apply(GroovyPlugin)

        def rules = GroovyTestRule.generator(project, new DependencyCache(project, new PreExistingRules()))
        assert rules.isEmpty()
    }

    @Test
    public void doNotCreateJavaLibraryRuleWhenJavaPluginDoesNotExist() {
        def rules = GroovyTestRule.generator(project, new DependencyCache(project, new PreExistingRules()))
        assert rules.isEmpty()
    }
}