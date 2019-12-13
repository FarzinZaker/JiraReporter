package jirareporter

class Team {

    String name
    String xoName
    Integer xoKey
    Integer xoManagerId

    static constraints = {
        xoManagerId nullable: true
    }

    @Override
    String toString() {
        name
    }
}
