package jirareporter

class Issue {

    String key
    IssueType issueType
    JiraUser assignee
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
    String summary
    Priority priority
    Double aggregateProgressValue
    Double aggregateProgressTotal
    Double aggregateProgressPercent

    transient Map assignees = [:]

    static hasMany = [components: Component, clients: Client]

    static mapping = {
        key column: 'jira_key'
    }

    static constraints = {
        assignee nullable: true
        summary nullable: true
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

    }

    @Override
    String toString(){
        key
    }
}
