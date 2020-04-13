package jirareporter

class RepositoryCodingLanguage {

    Repository repository
    CodingLanguage language
    Long linesOfCode

    static constraints = {
    }

    @Override
    String toString(){
        "$language: $linesOfCode"
    }
}
