package uk.gov.dwp.buckify.rules

import groovy.transform.Canonical

class PreExistingRules {

    Map<String, PreExistingRule> rules = [:]

    def load(URL url) {
        parse(url.text, url.file)
    }

    def load(File file) {
        parse(file.text, file.path)
    }

    PreExistingRules parse(String content, String source) {
        Map<String, PreExistingRule> rules = [:]
        def matcher = content =~ /(?m)^\s*(?<!#)(?<type>\w*)\s*\([^\)]*name\s*=\s*['"](?<name>[^'"]*)[^\)]+/

        while (matcher.find()) {
            matcher.group('type')
            def name = matcher.group('name')
            rules.put(name, new PreExistingRule(name, matcher.group('type'), source))
        }
        this.rules << rules
        this
    }

    boolean contains(String ruleName) {
        rules.containsKey(ruleName)
    }

    def findPath(String name) {
        rules.get(name)?.source
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
