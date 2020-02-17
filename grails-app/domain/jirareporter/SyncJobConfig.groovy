package jirareporter

class SyncJobConfig {

    String name
    Date startDate
    Date endDate
    Long lastRecord
    Date lastExecutionDate
    String lastErrorMessage
    Long executionTime

    static constraints = {
        startDate nullable: true
        endDate nullable: true
        lastRecord nullable: true
        lastExecutionDate nullable: true
        lastErrorMessage nullable: true
        executionTime nullable: true
    }

    static mapping = {
        version false
        lastErrorMessage sqlType: 'text'
    }
}
