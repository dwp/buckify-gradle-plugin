package uk.gov.dwp.buckify

import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction
import uk.gov.dwp.buckify.dependencies.DependencyCache
import uk.gov.dwp.buckify.rules.PreExistingRules
import uk.gov.dwp.buckify.rules.Rule

class BuckifyTask extends DefaultTask {

    static final String NAME = "buckify"

    @TaskAction
    public void convert() {
        createBuckFile(project, PreExistingRules.find(project)).writeToFile()
    }

    BuckFile createBuckFile(Project project, PreExistingRules preExistingRules) {
        new BuckFile(project, createRules(project, preExistingRules))
    }

    List<Rule> createRules(Project project, PreExistingRules preExistingRules) {
        DependencyCache dependencies = new DependencyCache(project, preExistingRules)
        BuckifyExtension.from(project).ruleGenerators.collect { it(project, dependencies) }.flatten()
    }

}
