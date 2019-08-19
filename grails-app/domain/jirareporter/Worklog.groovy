package jirareporter

class Worklog {

    String url
    JiraUser author
    JiraUser updateAuthor
    String comment
    Date created
    Date updated
    Date started
    String timeSpent
    Long timeSpentSeconds
    String jiraId
    String issueId
    Issue task
    Project project

    static mapping = {
        comment sqlType: 'text'
    }

    static constraints = {
        updateAuthor nullable: true
        task nullable: true
        project nullable: true
        comment nullable: true
    }
}
