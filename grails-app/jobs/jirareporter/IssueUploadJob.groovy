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

        def issueUploadItems = IssueUploadItem.createCriteria().list {
            lt('retryCount', 20)
            order('time')
            maxResults(100)
        }
//        def threads = []
        issueUploadItems.each { IssueUploadItem issueUploadItem ->
//            threads << Thread.start {
//                Issue.withNewTransaction {
            issueUploadService.update(issueUploadItem.issue, issueUploadItem.time, issueUploadItem.creator)
//                }
//            }
        }
//        threads.each { it.join() }
    }
}
