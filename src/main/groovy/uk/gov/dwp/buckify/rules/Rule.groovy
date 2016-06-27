package uk.gov.dwp.buckify.rules

import groovy.transform.Canonical
import uk.gov.dwp.buckify.dependencies.BuckDependency
import uk.gov.dwp.buckify.dependencies.Dependencies

@Canonical
abstract class Rule {

    String name
    Set<String> visibility = ["PUBLIC"]

    void print(PrintStream stream) {
        def output = createOutput()
        stream.print(output)
        System.out.print(output)
    }

    abstract Writable createOutput()

    static Collection<String> quoteAndSort(Collection<String> strings) {
        strings.toSet().collect({ '"' + it + '"' }).sort()
    }

    static Collection<String> pathsTo(Set<BuckDependency> dependencies) {
        dependencies.collect({ "                    '$it.path'" }).toSet().sort()
    }

    static String toPythonBoolean(boolean val) {
        String.valueOf(val).capitalize()
    }

    protected String formatted(Dependencies dependencies) {
        def nonTransitiveDeps = pathsTo(dependencies.nonTransitiveDependencies()).collect({ "$it," }).join("\n")
        def transitiveDeps = pathsTo(dependencies.transitiveDependencies).join(',\n')
        """deps=[
$nonTransitiveDeps
                    #transitive deps
$transitiveDeps
                ],"""
    }
}
