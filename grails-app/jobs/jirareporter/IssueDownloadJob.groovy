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

        while (IssueDownloadItem.count() > 0) {
            def issueDownloadItem = IssueDownloadItem.findByIdGreaterThan(0)
            issueDownloadService.download(issueDownloadItem.issue.key)
            IssueDownloadItem.executeUpdate("delete IssueDownloadItem where issue = :issue", [issue: issueDownloadItem.issue])
        }
    }
}
