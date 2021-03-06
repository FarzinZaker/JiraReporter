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
//    String teamName
    Team team

    static mapping = {
        key column: 'jira_key'
        version false
    }

    static constraints = {
        emailAddress nullable: true
        timezone nullable: true
        avatar nullable: true
//        teamName nullable: true
        team nullable: true
    }

    @Override
    String toString() {
        displayName
    }
}
