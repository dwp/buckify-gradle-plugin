package uk.gov.dwp.buckify

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test

import static org.junit.Assert.assertTrue

class BuckifyPluginTest {
    @Test
    public void pluginShouldAddTaskToProject() {
        Project project = ProjectBuilder.builder().build()
        project.plugins.apply 'uk.gov.dwp.buckify'

        assertTrue(project.tasks.buckify instanceof BuckifyTask)
    }
}