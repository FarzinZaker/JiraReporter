package jirareporter

class IssueLink {

    String key
    String type
    Issue firstIssue
    Issue secondIssue

    static mapping = {
        key column: 'jira_key'
        version false
    }

    static constraints = {
    }
}
