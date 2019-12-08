package jirareporter

class IssueUploadJob {
    static triggers = {
        simple repeatInterval: 5000l // execute job once in 5 seconds
    }

    static concurrent = false

    def issueUploadService

    def execute() {
        def issueUploadItem = IssueUploadItem.findByIdGreaterThan(0)
        issueUploadService.update(issueUploadItem.issue)
//        println 'upload complete'
    }
}
