package uk.gov.dwp.buckify.dependencies

trait BuckDependency implements Comparable<BuckDependency> {
    String name
    String path
    String identifier

    @Override
    int compareTo(BuckDependency o) {
        return name.compareTo(o.name)
    }
}
