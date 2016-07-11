package uk.gov.dwp.buckify.dependencies

trait BuckDependency implements Comparable<BuckDependency> {
    String name
    String path

    @Override
    int compareTo(BuckDependency o) {
        return name.compareTo(o.name)
    }
}
