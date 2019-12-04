package jirareporter

class IssueUploadItem {

    Issue issue
    String property
    String errorMessage
    Date lastTry
    Date dateCreated
    Date lastUpdated

    static constraints = {
        errorMessage nullable: true
        lastTry nullable: true
    }

    static mapping = {
        version false
    }
}
