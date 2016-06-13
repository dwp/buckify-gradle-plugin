package uk.gov.dwp.buckify

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test

class BuckifyTaskTest {
    @Test
    public void loadDependenciesAndCreateBuckFiles() {

        def start = System.currentTimeMillis()

        Project testProject = ProjectBuilder.builder().withProjectDir(new File("src/test/resources/dummy-java-groovy-project")) build()
        def extension = setupProject(testProject)
        extension.preExistingRuleFiles = ["lib/BUCK"]

//        Project childProject = ProjectBuilder.builder().withName("child").withParent(testProject).build()
//        setupProject(childProject)
//
//        childProject.dependencies{
//            compile 'commons-lang:commons-lang:2.5'
//        }

        testProject.dependencies {
//            compile project(path: ':child')
//            compile 'commons-lang:commons-lang:2.6'
//            compile 'joda-time:joda-time:2.2'
//            compile group: 'org.apache.solr', name: 'solr-core', version: '6.0.1'

            testCompile group: 'junit', name: 'junit', version: '4.11'
//            testCompile "uk.gov.dwp.common:mongo-ext:1.951.0:tests"
        }

        def buckifyTask = testProject.task('buckify', type: BuckifyTask)
        buckifyTask.actions.each { it.execute(buckifyTask) }

        println System.currentTimeMillis() - start + "ms"
    }

    private BuckifyExtension setupProject(Project myProject) {
        def extension = myProject.extensions.create("buckify", BuckifyExtension)
        myProject.plugins.apply('java')
        myProject.plugins.apply('groovy')
        myProject.repositories {
            mavenLocal()
            maven { url 'http://bld1.infra.uk1.uc:8081/nexus/content/repositories/releases' }
            maven { url 'http://bld1.infra.uk1.uc:8081/nexus/content/groups/public/' }
            maven { url 'http://bld1.infra.uk1.uc:8081/nexus/content/repositories/snapshots' }
            maven { url 'https://repo.gradle.org/gradle/libs' }
        }
        extension
    }
}