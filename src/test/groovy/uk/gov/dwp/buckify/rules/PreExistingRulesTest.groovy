package uk.gov.dwp.buckify.rules

import org.junit.Test

class PreExistingRulesTest {

    @Test
    public void loadRulesFromFile() {
        def testRulesFile = getClass().getResource("/test_rules")
        def rules = new PreExistingRules().load(testRulesFile)

        assert rules.rules.size() == 4
        assert rules.rules["activemq-broker"] == new PreExistingRules.PreExistingRule("activemq-broker", "from_nexus", "test_rules")
        assert rules.rules["activemq-jms-pool"] == new PreExistingRules.PreExistingRule("activemq-jms-pool", "prebuilt_jar", "test_rules")
        assert rules.rules["activemq-jms-pool-mvn"] == new PreExistingRules.PreExistingRule("activemq-jms-pool-mvn", "remote_file", "test_rules")
        assert rules.rules["test"] == new PreExistingRules.PreExistingRule("test", "java_test", "test_rules")
    }
}
