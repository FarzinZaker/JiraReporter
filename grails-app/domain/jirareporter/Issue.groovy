package jirareporter

class Issue {

    String key
    IssueType issueType
    JiraUser assignee
    String originalEstimate
    Long originalEstimateSeconds
    String remainingEstimate
    Long remainingEstimateSeconds
    String timeSpent
    Long timeSpentSeconds
    Status status
    JiraUser reporter
    Double progressValue
    Double progressTotal
    Double progressPercent
    Project project
    Date updated
    Date created
    String summary
    String description
    Priority priority
    Double aggregateProgressValue
    Double aggregateProgressTotal
    Double aggregateProgressPercent
    Date startDate
    Date dueDate

    Date lastSync

    Date lastFix

    Issue parent

    transient Map assignees = [:]

    static hasMany = [components: Component, clients: Client]

    static mapping = {
        key column: 'jira_key'
        description sqlType: 'text'
        version false
    }

    static constraints = {
        assignee nullable: true
        summary nullable: true
        description nullable: true
        originalEstimate nullable: true
        originalEstimateSeconds nullable: true
        remainingEstimate nullable: true
        remainingEstimateSeconds nullable: true
        timeSpent nullable: true
        timeSpentSeconds nullable: true
        progressValue nullable: true
        progressTotal nullable: true
        progressPercent nullable: true
        aggregateProgressValue nullable: true
        aggregateProgressTotal nullable: true
        aggregateProgressPercent nullable: true
        parent nullable: true
        startDate nullable: true
        dueDate nullable: true
        updated nullable: true
        created nullable: true
        lastFix nullable: true
        lastSync nullable: true
    }

    @Override
    String toString() {
        key
    }
}
