package jirareporter

class Label {

    String name

    static constraints = {
    }

    static mapping = {
        version false
    }

    @Override
    String toString(){
        name
    }
}
