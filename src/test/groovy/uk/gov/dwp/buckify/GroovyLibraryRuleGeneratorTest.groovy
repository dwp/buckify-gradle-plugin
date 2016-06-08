package uk.gov.dwp.buckify

import org.gradle.api.Project
import org.gradle.api.plugins.GroovyPlugin
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test
import uk.gov.dwp.buckify.dependencies.DependencyCache
import uk.gov.dwp.buckify.rules.GroovyLibraryRule

class GroovyLibraryRuleGeneratorTest {

    final Project project = ProjectBuilder.builder().build()

    @Test
    public void createGroovyLibraryRuleWhenPredicateIsTrue() {
        project.extensions.create("buckify", BuckifyExtension).groovyLibraryPredicate = { true }
        project.plugins.apply(GroovyPlugin)

        def rules = GroovyLibraryRule.generator(project, new DependencyCache(project))
        assert rules.size() == 1
        assert rules.first() instanceof GroovyLibraryRule
    }

    @Test
    public void doNotCreateGroovyLibraryRuleWhenPredicateIsFalse() {
        project.extensions.create("buckify", BuckifyExtension).groovyLibraryPredicate = { false }
        project.plugins.apply(GroovyPlugin)
        project.dependencies {}

        def rules = GroovyLibraryRule.generator(project, new DependencyCache(project))
        assert rules.isEmpty()
    }
}
