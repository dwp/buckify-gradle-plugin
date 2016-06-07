package uk.gov.dwp.buckify.rules

import groovy.transform.Canonical

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

    static String toPythonBoolean(boolean val){
        String.valueOf(val).capitalize()
    }
}
