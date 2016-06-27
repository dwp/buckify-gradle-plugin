package uk.gov.dwp.buckify

import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction
import uk.gov.dwp.buckify.dependencies.DependencyCache
import uk.gov.dwp.buckify.rules.GroovyLibraryRule
import uk.gov.dwp.buckify.rules.GroovyTestRule
import uk.gov.dwp.buckify.rules.JavaLibraryRule
import uk.gov.dwp.buckify.rules.JavaTestRule
import uk.gov.dwp.buckify.rules.PreBuiltJarRule
import uk.gov.dwp.buckify.rules.PreExistingRules
import uk.gov.dwp.buckify.rules.RemoteFileRule
import uk.gov.dwp.buckify.rules.Rule

class BuckifyTask extends DefaultTask {

    static final String NAME = "buckify"

    def ruleGenerators = [
            JavaLibraryRule.generator,
            GroovyLibraryRule.generator,
            JavaTestRule.generator,
            GroovyTestRule.generator,
            PreBuiltJarRule.generator,
            RemoteFileRule.generator
    ]

    @TaskAction
    public void convert() {
        createBuckFile(project, PreExistingRules.find(project)).writeToFile()
    }

    BuckFile createBuckFile(Project project, PreExistingRules preExistingRules) {
        new BuckFile(project, createRules(project, preExistingRules))
    }

    List<Rule> createRules(Project project, PreExistingRules preExistingRules) {
        DependencyCache dependencies = new DependencyCache(project, preExistingRules)
        ruleGenerators.collect { it(project, dependencies) }.flatten()
    }

}
