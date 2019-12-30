package jirareporter

class SyncJobConfig {

    String name
    Date startDate
    Date endDate
    Long lastRecord

    static constraints = {
        startDate nullable: true
        endDate nullable: true
        lastRecord nullable: true
    }

    static mapping = {
        version false
    }
}
