package uk.gov.dwp.buckify.rules

import groovy.transform.Canonical
import org.gradle.api.Project
import uk.gov.dwp.buckify.BuckifyExtension

class PreExistingRules {

    Map<String, PreExistingRule> rules = [:]

    static def find(Project project) {
        PreExistingRules preExistingRules = new PreExistingRules()
        BuckifyExtension.from(project).preExistingRuleFiles.each {
            def file = project.file(it)
            preExistingRules.parse(file.text, project.rootDir.toPath().relativize(file.parentFile.toPath()).toString())
        }
        preExistingRules
    }

    PreExistingRules parse(String content, String source) {
        System.out.println "Loaded rules from $source"

        Map<String, PreExistingRule> rules = [:]
        def matcher = content =~ /(?m)^\s*(?<!#)(?<type>\w*)\s*\([^\)]*name\s*=\s*['"](?<name>[^'"]*)[^\)]+/

        while (matcher.find()) {
            matcher.group('type')
            def name = matcher.group('name')
            def rule = new PreExistingRule(name, matcher.group('type'), source)
            System.out.println("Found rule: name=$rule.name, type=$rule.type, source=$rule.source")
            rules.put(name, rule)
        }
        this.rules << rules
        this
    }

    boolean contains(String... ruleNames) {
        ruleNames.any { rules.containsKey(it) }
    }

    def findPath(String name) {
        rules.containsKey(name) ? "//${rules.get(name).source}:$name" : null
    }

    @Canonical
    protected static class PreExistingRule {

        private final String type
        private final String name
        private final String source

        PreExistingRule(String name, String type, String source) {
            this.source = source
            this.name = name
            this.type = type
        }
    }
}
