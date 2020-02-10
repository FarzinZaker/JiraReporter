package jirareporter

import grails.util.Environment

class IssueLinkUploadJob {
    static triggers = {
        simple repeatInterval: 5000l // execute job once in 5 seconds
    }

    static concurrent = false

    def issueLinkUploadService

    def execute() {

        if(Environment.isDevelopmentMode())
            return

        //remove unnecessary links
        IssueLink.findAllByAddedAndDeleted(true, true).each { it.delete() }

        //delete removed links
        IssueLink.findAllByAddedAndDeletedAndKeyNotEqual(false, true, '-').each { link ->
            issueLinkUploadService.removeLink(link)
        }

        //create new Links
        IssueLink.findAllByAddedAndDeleted(true, false).each { link ->
            issueLinkUploadService.addLink(link)
        }

        //update added link
        IssueLink.findAllByAddedAndDeletedAndKey(false, true, '-').each { link ->
            new IssueDownloadItem(issueKey: link.firstIssue.key, source: 'Fill Empty Link Keys').save()
        }
    }
}
