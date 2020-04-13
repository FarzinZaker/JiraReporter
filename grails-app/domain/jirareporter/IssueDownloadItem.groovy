package jirareporter

class IssueDownloadItem {

    String issueKey
    String source
    String errorMessage
    Date lastTry
    Integer retryCount = 0
    Date dateCreated
    Date lastUpdated

    static constraints = {
        errorMessage nullable: true
        lastTry nullable: true
        source nullable: true
        retryCount nullable: true
    }

    static mapping = {
        version false
    }
}
