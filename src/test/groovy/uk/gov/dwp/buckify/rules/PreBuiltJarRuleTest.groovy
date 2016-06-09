package uk.gov.dwp.buckify.rules

import org.junit.Test

import static org.junit.Assert.assertEquals

class PreBuiltJarRuleTest {

    @Test
    public void createOutput() {
        def underTest = new PreBuiltJarRule("name", "binary", false)

        assertEquals "prebuilt_jar(name='name', binary_jar='binary', visibility=[\"PUBLIC\"])\n", underTest.createOutput().toString()
    }
}
