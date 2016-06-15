package uk.gov.dwp.buckify.rules

import groovy.transform.Canonical
import org.gradle.api.Project
import uk.gov.dwp.buckify.BuckifyExtension

import java.nio.file.Path

class PreExistingRules {

    Map<String, PreExistingRule> rules = [:]

    static def find(Project project) {
        PreExistingRules preExistingRules = new PreExistingRules()
        BuckifyExtension.from(project).preExistingRuleFiles.each {
            def file = project.file(it)
            preExistingRules.parse(file.text, pathRelativeToRootDir(file, project).toString())
        }
        preExistingRules
    }

    private static Path pathRelativeToRootDir(File file, Project project) {
        project.rootDir.toPath().relativize(file.parentFile.toPath())
    }

    PreExistingRules parse(String content, String path) {
        System.out.println "Loaded rules from $path"

        Map<String, PreExistingRule> rules = [:]
        def matcher = content =~ /(?m)^\s*(?<!#)(?<type>\w*)\s*\([^\)]*name\s*=\s*['"](?<name>[^'"]*)[^\)]+/

        while (matcher.find()) {
            matcher.group('type')
            def name = matcher.group('name')
            def rule = new PreExistingRule(name, matcher.group('type'), path)
            System.out.println("Found rule: name=$rule.name, type=$rule.type, source=$rule.path")
            rules.put(name, rule)
        }
        this.rules << rules
        this
    }

    boolean contains(String... ruleNames) {
        ruleNames.any { rules.containsKey(it) }
    }

    String resolvePath(String name) {
        rules.containsKey(name) ? "//${rules.get(name).path}:$name" : ":$name"
    }

    @Canonical
    protected static class PreExistingRule {

        private final String type
        private final String name
        private final String path

        PreExistingRule(String name, String type, String path) {
            this.path = path
            this.name = name
            this.type = type
        }
    }
}
