package jirareporter

class IssueDownloadItem {

    Issue issue
    String source
    String errorMessage
    Date lastTry
    Date dateCreated
    Date lastUpdated

    static constraints = {
        errorMessage nullable: true
        lastTry nullable: true
        source nullable: true
    }

    static mapping = {
        version false
    }
}
