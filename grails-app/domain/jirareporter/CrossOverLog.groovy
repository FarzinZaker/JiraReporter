package jirareporter

class CrossOverLog {

//    String teamName
    Team team
    String name
    Date date
    Double hours

    static constraints = {
        team nullable: true
    }

    static mapping = {
//        teamName column: 'team'
        version false
    }
}
