package uk.gov.dwp.buckify.dependencies

import groovy.transform.Canonical

@Canonical
class ArtifactDependency implements BuckDependency {

    String filename
    String sha1
    String group
    String version
    String mavenIdentifier

    ArtifactDependency(String name, String group, String version, String path, String mavenIdentifier, String filename, String sha1) {
        this.name = name
        this.group = group
        this.version = version
        this.path = path
        this.mavenIdentifier = mavenIdentifier
        this.filename = filename
        this.sha1 = sha1
    }
}