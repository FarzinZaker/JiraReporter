package jirareporter

class TeamManager implements Serializable {

    Team team
    User manager

    static mapping = {
        id composite: ['team', 'manager']
        version false
    }

    static constraints = {
    }
}
