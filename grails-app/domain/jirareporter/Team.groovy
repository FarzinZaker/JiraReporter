package jirareporter

class Team implements Serializable {

    String name
    String xoName
    Integer xoKey
    Integer xoManagerId

    static constraints = {
        xoManagerId nullable: true
    }
    static mapping = {
        version false
    }

    @Override
    String toString() {
        name
    }
}
