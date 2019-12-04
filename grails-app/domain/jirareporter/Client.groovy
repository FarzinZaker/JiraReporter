package jirareporter

class Client {

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
