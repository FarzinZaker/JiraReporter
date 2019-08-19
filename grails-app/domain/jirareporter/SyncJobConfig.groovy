package jirareporter

class SyncJobConfig {

    String name
    Date startDate
    Date endDate

    static constraints = {
        startDate nullable: true
        endDate nullable: true
    }
}
