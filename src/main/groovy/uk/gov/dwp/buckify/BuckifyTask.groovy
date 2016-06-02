package uk.gov.dwp.buckify

import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction
import uk.gov.dwp.buckify.rules.*

class BuckifyTask extends DefaultTask {

    static final String NAME = "buckify"

    def ruleGenerators = [JavaLibraryRule.generator, GroovyLibraryRule.generator, JavaTestRule.generator, PreBuiltJarRule.generator]

    @TaskAction
    public void convert() {
        createBuckFiles(project).each { it.writeToFile() }
    }

    List<BuckFile> createBuckFiles(Project project) {
        def files = project.childProjects.values().collect() {
            this.createBuckFiles(it)
        }.flatten()
        files << new BuckFile(project, createRules(project))
    }

    List<Rule> createRules(Project project) {
        ruleGenerators.collect { it(project) }.flatten()
    }

}
