package jirareporter

import grails.util.Environment

class IssueUploadJob {
    static triggers = {
        simple repeatInterval: 5000l // execute job once in 5 seconds
    }

    static concurrent = false

    def issueUploadService

    def execute() {

        if (!Environment.isDevelopmentMode())
            return

        def issueUploadItems = IssueUploadItem.findAllByRetryCountLessThan(20, [max: 100])
//        def threads = []
        issueUploadItems.each { issueUploadItem ->
//            threads << Thread.start {
//                Issue.withNewTransaction {
                    issueUploadService.update(issueUploadItem.issue, issueUploadItem.creator)
//                }
//            }
        }
//        threads.each { it.join() }
    }
}
