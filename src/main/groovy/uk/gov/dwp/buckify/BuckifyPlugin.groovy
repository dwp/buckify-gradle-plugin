package uk.gov.dwp.buckify

import org.gradle.api.Plugin
import org.gradle.api.Project

class BuckifyPlugin implements Plugin<Project> {

    void apply(Project project) {
        if (!project.extensions.findByName(BuckifyExtension.NAME))
            project.extensions.create(BuckifyExtension.NAME, BuckifyExtension)

        BuckifyTask buckifyTask = project.tasks.create(BuckifyTask.NAME, BuckifyTask)
        buckifyTask.outputs.upToDateWhen { false }
    }
}
