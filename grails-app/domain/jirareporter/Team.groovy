package jirareporter

class Team implements Serializable {

    String name
    String xoName
    Integer xoKey
    Integer xoManagerId
    Boolean deleted = false

    static constraints = {
        xoManagerId nullable: true
        deleted nullable: true
    }
    static mapping = {
        version false
    }

    @Override
    String toString() {
        name
    }
}
