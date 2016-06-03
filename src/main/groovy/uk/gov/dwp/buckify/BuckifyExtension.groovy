package uk.gov.dwp.buckify

import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.ExternalDependency
import org.gradle.api.artifacts.ProjectDependency
import org.gradle.api.artifacts.ResolvedArtifact
import org.gradle.api.plugins.GroovyPlugin

class BuckifyExtension {
    static final String NAME = "buckify"

    String outputType
    Closure projectDependencyRuleResolution = { ProjectDependency dep -> dep.name + ":" + javaLibrary.defaultRuleName }
    Closure externalDependencyRuleResolution = { ExternalDependency dep -> "//lib:" + dep.name }
    Closure resolvedExternalDependencyRuleResolution = { ResolvedArtifact dep -> "//lib:" + dep.name }
    Closure binaryJarResolution = { String dep -> ":" + dep + "-mvn" }
    GroovyLibrary groovyLibrary = new GroovyLibrary()
    JavaTestLibrary javaTestLibrary = new JavaTestLibrary()
    JavaLibrary javaLibrary = new JavaLibrary()
    boolean autoDeps = true

    BuckifyExtension(String outputType = "file") {
        this.outputType = outputType
    }

    static BuckifyExtension from(Project project) {
        project.extensions.findByType(BuckifyExtension)
    }

    class GroovyLibrary {
        Closure predicate = { Project project -> project.plugins.hasPlugin(GroovyPlugin) && project.file("src/main/groovy").exists() }
        String defaultRuleName = "main-groovy"
    }

    class JavaLibrary {
        String defaultRuleName = "main"
    }

    class JavaTestLibrary {
        String defaultRuleName = "test"
    }
}
