package jirareporter

class RecurringTask {

    RecurringTaskSetting setting
    String key
    Integer year
    Integer month
    Integer week

    static mapping = {
        key column: 'jira_key'
    }

    static constraints = {
        week nullable: true
    }
}
