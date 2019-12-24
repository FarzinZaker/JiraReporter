package jirareporter

class IssueUploadItem {

    Issue issue
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
