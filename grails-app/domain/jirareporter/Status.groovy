package jirareporter

class Status {

    String url
    String name
    String icon

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
