package uk.gov.dwp.buckify

import org.gradle.api.Project
import org.gradle.api.plugins.GroovyPlugin

class BuckifyExtension {

    static final String NAME = "buckify"

//    Closure<String> projectDependencyRuleName = { ProjectDependency dep -> "//$dep.name:" + javaLibraryRuleName }
//    Closure<String> externalDependencyRuleName = { ArtifactDependency dep -> (dep.identifier.contains("uk.gov.dwp") ? "//lib/internal:" : "//lib:") + dep.name }
    Closure<String> binaryJarRuleName = { String dep -> ":$dep-mvn" }

    String groovyLibraryRuleName = "groovy"
    String javaLibraryRuleName = "main"
    String javaTestRuleName = "test"
    Closure groovyLibraryPredicate = { Project project ->
        project.plugins.hasPlugin(GroovyPlugin) && project.file("src/main/groovy").exists()
    }
    boolean autoDeps = true

    List<String> preExistingRuleFiles = []

    static BuckifyExtension from(Project project) {
        project.extensions.findByType(BuckifyExtension)
    }
}
