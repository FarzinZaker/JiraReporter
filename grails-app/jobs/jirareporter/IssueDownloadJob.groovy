package jirareporter

import grails.util.Environment

class IssueDownloadJob {
    static triggers = {
        simple repeatInterval: 5000l // execute job once in 5 seconds
    }

    static concurrent = false

    def issueDownloadService


    def execute() {

        if (!Environment.isDevelopmentMode())
            return

//        if (IssueDownloadItem.count() > 0) {
//            def issueDownloadItems = IssueDownloadItem.findAllByIdGreaterThan(0, [max: 20])
////            def threads = []
//            issueDownloadItems.each { issueDownloadItem ->
////                threads << Thread.start {
////                    try {
////                        Issue.withNewTransaction {
//                            issueDownloadService.download(issueDownloadItem.issue.key)
//                            IssueDownloadItem.executeUpdate("delete IssueDownloadItem where issue = :issue", [issue: issueDownloadItem.issue])
////                        }
////                    } catch (ex) {
////                        println ex.message
////                    }
////                }
//            }
////            threads.each { it.join() }
//        }
    }
}
