package uk.gov.dwp.buckify

import org.apache.commons.io.IOUtils
import org.gradle.api.Project
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import uk.gov.dwp.buckify.rules.Rule

import java.util.function.Function
import java.util.stream.Stream

class BuckFile {
    final Project project
    final List<Rule> rules

    BuckFile(Project project, List<Rule> rules) {
       this.project = project
        this.rules = rules
    }

    void writeToFile() {
        def buckFile = project.file("BUCK")
        buckFile.createNewFile()
        PrintStream stream = new PrintStream(buckFile)
        rules.each { it.print(stream) }
        IOUtils.closeQuietly(stream)
    }
}
