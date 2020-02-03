package jirareporter

class Filter {

    String name
    User owner
    String data

    static mapping = {
        data sqlType: 'text'
    }

    static constraints = {
    }
}
