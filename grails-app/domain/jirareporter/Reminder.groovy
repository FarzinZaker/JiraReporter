package jirareporter

class Reminder {

    String name
    String template
    String query
    String emptyMessage

    static constraints = {
        emptyMessage nullable: true
    }
}
