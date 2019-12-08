package jirareporter

class IssueUploadJob {
    static triggers = {
        simple repeatInterval: 5000l // execute job once in 5 seconds
    }

    static concurrent = false

    def issueUploadService

    def execute() {
        def issueUploadItems = IssueUploadItem.findAllByIdGreaterThan(0)
        def threads = []
        issueUploadItems.each { issueUploadItem ->
            threads << Thread.start {
                Issue.withNewTransaction {
                    issueUploadService.update(issueUploadItem.issue)
                }
            }
        }
        threads.each { it.join() }

//        println 'upload complete'
    }
}
