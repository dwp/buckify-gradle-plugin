package uk.gov.dwp.buckify.rules

import org.gradle.api.Project

class RemoteFileRule extends Rule {

    RemoteFileRule(Project project) {
    }

    @Override
    Writable createOutput() {
        return null
    }
}
