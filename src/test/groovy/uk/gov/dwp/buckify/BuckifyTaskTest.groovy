package uk.gov.dwp.buckify

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test

class BuckifyTaskTest {
    @Test
    public void loadDependenciesAndCreateBuckFiles() {

        def start = System.currentTimeMillis()

        Project parentProject = ProjectBuilder.builder().withProjectDir(new File("src/test/resources/dummy-java-groovy-project")) build()
        def extension = setupProject(parentProject)
        extension.preExistingRuleFiles = ["lib/BUCK"]

        Project childProject = ProjectBuilder.builder().withName("child").withParent(parentProject).build()
        setupProject(childProject)
        Project testSupportProject = ProjectBuilder.builder().withName("test-support").withParent(childProject).build()
        setupProject(testSupportProject)
        Project anotherChildProject = ProjectBuilder.builder().withName("another-child").withParent(parentProject).build()
        setupProject(anotherChildProject)

        childProject.dependencies{
            compile 'commons-lang:commons-lang:2.5'
        }

        anotherChildProject.dependencies {
            testCompile project(path: ":child:test-support")
        }

        parentProject.dependencies {
            compile project(path: ':child')
            testCompile group: 'junit', name: 'junit', version: '4.11'
        }

        executeBuckifyTask(parentProject)
        executeBuckifyTask(childProject)
        executeBuckifyTask(testSupportProject)
        executeBuckifyTask(anotherChildProject)

        println System.currentTimeMillis() - start + "ms"
    }

    private void executeBuckifyTask(Project parentProject) {
        def buckifyTask = parentProject.task('buckify', type: BuckifyTask)
        buckifyTask.actions.each { it.execute(buckifyTask) }
    }

    private BuckifyExtension setupProject(Project myProject) {
        def extension = myProject.extensions.create("buckify", BuckifyExtension)
        myProject.projectDir.mkdirs()
        myProject.plugins.apply('java')
        myProject.plugins.apply('groovy')
        myProject.repositories {
            mavenLocal()
            maven { url 'http://repo.gradle.org/gradle/libs' }
        }
        extension
    }
}