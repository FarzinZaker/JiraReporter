package jirareporter

class IssueUploadItem {

    String issueKey
//    Issue issue
    String property
    String value
    String comment
    Date time
    User creator
    String source
    String errorMessage
    Integer retryCount = 0
    Date lastTry
    Date dateCreated
    Date lastUpdated

    static constraints = {
        issueKey nullable: true
        errorMessage nullable: true
        lastTry nullable: true
        source nullable: true
        comment nullable: true
        creator nullable: true
        time nullable: true
    }

    static mapping = {
        version false
    }
}
