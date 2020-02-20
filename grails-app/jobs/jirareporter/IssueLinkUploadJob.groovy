package jirareporter

import grails.util.Environment

class IssueLinkUploadJob {
    static triggers = {
        simple repeatInterval: 5000l // execute job once in 5 seconds
    }

    static concurrent = false

    def issueLinkUploadService
    def jobExecutionService
    def issueDownloadService

    def execute() {

        if (!jobExecutionService.jobsEnabled())
            return

        //remove unnecessary links
        jobExecutionService.execute('Remove Unnecessary Links',
                { SyncJobConfig jobConfig ->
                    IssueLink.findAllByAddedAndDeleted(true, true).each { it.delete() }
                })

        //delete removed links
        jobExecutionService.execute('Clean Deleted Links',
                { SyncJobConfig jobConfig ->
                    IssueLink.findAllByAddedAndDeletedAndKeyNotEqual(false, true, '-').each { link ->
                        issueLinkUploadService.removeLink(link)
                    }
                })

        //create new Links
        jobExecutionService.execute('Upload Newly Created Links',
                { SyncJobConfig jobConfig ->
                    IssueLink.findAllByAddedAndDeleted(true, false).each { link ->
                        issueLinkUploadService.addLink(link)
                    }
                })

        //update added link
        jobExecutionService.execute('Download Issues With Removed Link',
                { SyncJobConfig jobConfig ->
                    IssueLink.findAllByAddedAndDeletedAndKey(false, true, '-').each { link ->
                        issueDownloadService.enqueue(link.firstIssue.key, 'Fill Empty Link Keys')
                    }
                })
    }
}
