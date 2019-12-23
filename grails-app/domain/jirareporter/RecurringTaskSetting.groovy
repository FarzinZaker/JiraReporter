package jirareporter

class RecurringTaskSetting {

    JiraUser user
    Project project
    Boolean enabled = false
    String originalEstimate = '1h'

    static hasMany = [components: Component]

    static constraints = {
    }
}
