package uk.gov.dwp.buckify.dependencies

import groovy.transform.Canonical

@Canonical
class ArtifactDependency implements BuckDependency {

    String filename
    String sha1

    ArtifactDependency(String name, String path, String identifier, String filename, String sha1) {
        this.name = name
        this.path = path
        this.identifier = identifier
        this.filename = filename
        this.sha1 = sha1
    }
}