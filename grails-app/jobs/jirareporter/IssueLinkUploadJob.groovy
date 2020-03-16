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
                { SyncJobConfig jobConfig, Date startDate, Date endDate, Long lastRecord ->
                    IssueLink.withTransaction {
                        IssueLink.findAllByAddedAndDeleted(true, true).each { it.delete() }
                    }
                    [
                            startDate: startDate,
                            endDate: endDate,
                            lastRecord: lastRecord
                    ]
                })

        //delete removed links
        jobExecutionService.execute('Clean Deleted Links',
                { SyncJobConfig jobConfig, Date startDate, Date endDate, Long lastRecord ->
                    IssueLink.withTransaction {
                        IssueLink.findAllByAddedAndDeletedAndKeyNotEqual(false, true, '-').each { link ->
                            issueLinkUploadService.removeLink(link)
                        }
                    }
                    [
                            startDate: startDate,
                            endDate: endDate,
                            lastRecord: lastRecord
                    ]
                })

        //create new Links
        jobExecutionService.execute('Upload Newly Created Links',
                { SyncJobConfig jobConfig, Date startDate, Date endDate, Long lastRecord ->
                    IssueLink.withTransaction {
                        IssueLink.findAllByAddedAndDeleted(true, false).each { link ->
                            issueLinkUploadService.addLink(link)
                        }
                    }
                    [
                            startDate: startDate,
                            endDate: endDate,
                            lastRecord: lastRecord
                    ]
                })

        //update added link
        jobExecutionService.execute('Download Issues With Removed Link',
                { SyncJobConfig jobConfig, Date startDate, Date endDate, Long lastRecord ->
                    IssueLink.withTransaction {
                        IssueLink.findAllByAddedAndDeletedAndKey(false, true, '-').each { link ->
                            issueDownloadService.enqueue(link.firstIssue.key, 'Fill Empty Link Keys')
                        }
                    }
                    [
                            startDate: startDate,
                            endDate: endDate,
                            lastRecord: lastRecord
                    ]
                })
    }
}
