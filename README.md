# buckify
Gradle plugin to create BUCK build files from the dependencies of a Gradle build

**Usage**: ./gradlew {project_name}:buckify

The plugin can be configured by adding a `buckify` section to the Gradle build file. Example:

    buckify {
        preExistingRuleFiles = ["$rootDir/lib/BUCK", "$rootDir/lib/internal/BUCK", "$rootDir/lib/transitive/BUCK"]
        nomenclature = { resolvedArtifact ->
            def id = resolvedArtifact.getModuleVersion().getId()
            if (id.name == "asm" && id.group == "net.minidev") {
                "minidev-asm"
            } else if (id.name == "asm" && id.group == "org.ow2.asm") {
                "ow2-asm"
            } else if (id.name == "javax.ws.rs-api") {
                "rs-api"
            } else {
                resolvedArtifact.name + (resolvedArtifact.classifier ? "-$resolvedArtifact.classifier" : "")
            }
        }
        excluded = { resolvedArtifact ->
            resolvedArtifact.name == 'groovy-all' && resolvedArtifact.classifier == "indy"
        }

        ruleGenerators = [
            uk.gov.dwp.buckify.rules.JavaLibraryRule.generator,
            uk.gov.dwp.buckify.rules.JavaTestRule.generator,
            uk.gov.dwp.buckify.rules.PreBuiltJarRule.generator,
            uk.gov.dwp.buckify.rules.RemoteFileRule.generator
        ]
    }
