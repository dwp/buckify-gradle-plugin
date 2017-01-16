package uk.gov.dwp.buckify

import org.gradle.api.Project
import org.gradle.api.artifacts.ResolvedArtifact
import org.gradle.api.plugins.GroovyPlugin
import uk.gov.dwp.buckify.rules.Rule

class BuckifyExtension {

    static final String NAME = "buckify"

    String groovyLibraryRuleName = "groovy-main"
    String groovyTestRuleName = "groovy-test"
    String javaLibraryRuleName = "main"
    String javaTestRuleName = "test"
    /**
     Paths of any pre-existing rule files e.g. a common file with BUCK dependency rules
     */
    List<String> preExistingRuleFiles = []
    boolean autoDeps = true

    /**
     Method used to name binary JAR rules
     */
    Closure<String> binaryJarRuleName = { String dep -> ":$dep-mvn" }
    /**
     Predicate used to to determine if a Groovy library rule should be generated for this project
     */
    Closure groovyLibraryPredicate = { Project project ->
        project.plugins.hasPlugin(GroovyPlugin) && project.file("src/main/groovy").exists()
    }
    /**
     Allows custom naming rules to be applied to certain artifcats
     */
    Closure<String> nomenclature = { ResolvedArtifact artifact ->
        artifact.name + (artifact.classifier ? "-$artifact.classifier" : "")
    }
    /**
     Predicate used to decide if an artifact should be excluded from the generate BUCK file
     */
    Closure<Boolean> excluded = { ResolvedArtifact artifact ->
        false
    }
    /**
     Allows users to define their own custom rule generators
     */
    Collection<Closure<Collection<? extends Rule>>> ruleGenerators = [
            uk.gov.dwp.buckify.rules.JavaLibraryRule.generator,
            uk.gov.dwp.buckify.rules.GroovyLibraryRule.generator,
            uk.gov.dwp.buckify.rules.JavaTestRule.generator,
            uk.gov.dwp.buckify.rules.GroovyTestRule.generator,
            uk.gov.dwp.buckify.rules.PreBuiltJarRule.generator,
            uk.gov.dwp.buckify.rules.RemoteFileRule.generator
    ]

    static BuckifyExtension from(Project project) {
        project.extensions.findByType(BuckifyExtension) ?: project.extensions.create("buckify", BuckifyExtension)
    }
}
