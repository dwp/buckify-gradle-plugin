package uk.gov.dwp.buckify

import org.gradle.api.Project
import org.gradle.api.artifacts.ResolvedArtifact
import org.gradle.api.plugins.GroovyPlugin

class BuckifyExtension {

    static final String NAME = "buckify"

    String groovyLibraryRuleName = "groovy-main"
    String javaLibraryRuleName = "main"
    String javaTestRuleName = "test"
    List<String> preExistingRuleFiles = []
    boolean autoDeps = true

    Closure<String> binaryJarRuleName = { String dep -> ":$dep-mvn" }
    Closure groovyLibraryPredicate = { Project project ->
        project.plugins.hasPlugin(GroovyPlugin) && project.file("src/main/groovy").exists()
    }
    Closure<String> nomenclature = { ResolvedArtifact artifact ->
        artifact.name + (artifact.classifier ? "-$artifact.classifier" : "")
    }

    static BuckifyExtension from(Project project) {
        project.extensions.findByType(BuckifyExtension) ?: project.extensions.create("buckify", BuckifyExtension)
    }
}
