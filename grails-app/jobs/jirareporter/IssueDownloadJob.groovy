package jirareporter

import grails.util.Environment

class IssueDownloadJob {
    static triggers = {
        simple repeatInterval: 5000l // execute job once in 5 seconds
    }

    static concurrent = false

    def issueDownloadService
    def jobExecutionService

    def execute() {

        if (!jobExecutionService.jobsEnabled())
            return


        jobExecutionService.execute('Download Manually Queued Issues',
                { SyncJobConfig jobConfig ->
                    def issueDownloadItems = IssueDownloadItem.findAllBySourceInList(['MANUAL', 'User'], [max: 100])
                    issueDownloadItems.each { issueDownloadItem ->
                        issueDownloadService.download(issueDownloadItem.issueKey)
                        issueDownloadItem.delete()
                    }
                })

        jobExecutionService.execute('Download All Queued Issues',
                { SyncJobConfig jobConfig ->
                    def issueDownloadItems = IssueDownloadItem.findAllByIdGreaterThan(0, [max: 100])
                    issueDownloadItems.each { issueDownloadItem ->
                        issueDownloadService.download(issueDownloadItem.issueKey)
                        issueDownloadItem.delete()
                    }
                })


    }
}
