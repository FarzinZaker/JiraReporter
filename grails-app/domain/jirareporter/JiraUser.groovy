package jirareporter

class JiraUser {

    String url
    String name
    String key
    String emailAddress
    String displayName
    Boolean active
    String timezone
    String avatar

    static mapping = {
        key column: 'jira_key'
    }

    static constraints = {
        emailAddress nullable: true
        timezone nullable: true
        avatar nullable: true
    }

    @Override
    String toString() {
        displayName
    }
}
