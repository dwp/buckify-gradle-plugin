package uk.gov.dwp.buckify.dependencies

trait BuckDependency implements Comparable<BuckDependency> {
    String ruleName
    String path
    String identifier

    @Override
    int compareTo(BuckDependency o) {
        return ruleName.compareTo(o.ruleName)
    }
}
