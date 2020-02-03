package jirareporter

class FilterUser {

    Filter filter
    User user

    static belongsTo = [Filter]

    static constraints = {
    }
}
