package jirareporter

import grails.util.Environment

class IssueDownloadJob {
    static triggers = {
        simple repeatInterval: 5000l // execute job once in 5 seconds
    }

    static concurrent = false

    def issueDownloadService


    def execute() {

        if (Environment.isDevelopmentMode())
            return

//        Date timer = new Date()
        def issueDownloadItems = IssueDownloadItem.findAllBySourceInList(['MANUAL', 'User'], [max: 100])
        issueDownloadItems.each { issueDownloadItem ->
            issueDownloadService.download(issueDownloadItem.issueKey)
            issueDownloadItem.delete()
//            IssueDownloadItem.executeUpdate("delete IssueDownloadItem where issueKey = :issueKey", [issueKey: issueDownloadItem.issueKey])
        }

        issueDownloadItems = IssueDownloadItem.findAllByIdGreaterThan(0, [max: 100])
        issueDownloadItems.each { issueDownloadItem ->
            issueDownloadService.download(issueDownloadItem.issueKey)
            issueDownloadItem.delete()
//            IssueDownloadItem.executeUpdate("delete IssueDownloadItem where issueKey = :issueKey", [issueKey: issueDownloadItem.issueKey])
        }

//        println('QUEUE:\t' + (new Date().time - timer.time))
    }
}
