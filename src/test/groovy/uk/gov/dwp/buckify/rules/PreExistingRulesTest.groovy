package uk.gov.dwp.buckify.rules

import org.junit.Test

class PreExistingRulesTest {

    @Test
    public void loadRulesFromFile() {
        def testRulesFile = getClass().getResource("/test_rules")
        def rules = new PreExistingRules().parse(testRulesFile.text, "path")

        assert rules.rules.size() == 4

        assertContains(rules, "test")
        assertContains(rules, "activemq-jms-pool")
        assertContains(rules, "activemq-jms-pool-mvn")
        assertContains(rules, "activemq-broker")

        assert !rules.contains("foo")
        assert rules.resolvePath("foo") == ":foo"
    }

    private void assertContains(PreExistingRules rules, String name) {
        assert rules.contains(name)
        assert rules.resolvePath(name) == "//path:$name"
    }
}
