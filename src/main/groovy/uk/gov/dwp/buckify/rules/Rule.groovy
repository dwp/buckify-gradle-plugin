package uk.gov.dwp.buckify.rules

import groovy.transform.Canonical
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

    static List<String> quoted(Collection<String> strings) {
        strings.collect { '"' + it + '"' }
    }

    static Set<String> transitiveDependencyPaths(Dependencies dependencies) {
        dependencies.transitiveDependencies.collect({ it.path }).toSet()
    }

    static String toPythonBoolean(boolean val){
        String.valueOf(val).capitalize()
    }
}
