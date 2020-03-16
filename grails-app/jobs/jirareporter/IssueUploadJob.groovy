package jirareporter

import grails.util.Environment

class IssueUploadJob {
    static triggers = {
        simple repeatInterval: 5000l // execute job once in 5 seconds
    }

    static concurrent = false

    def issueUploadService
    def jobExecutionService

    def execute() {

        if (!jobExecutionService.jobsEnabled())
            return

        jobExecutionService.execute('Upload Modified Issues',
                { SyncJobConfig jobConfig, Date startDate, Date endDate, Long lastRecord ->
                    IssueUploadItem.withTransaction {
                        def issueUploadItems = IssueUploadItem.createCriteria().list {
                            lt('retryCount', 20)
                            order('time')
                            maxResults(100)
                        }
                        issueUploadItems.each { IssueUploadItem issueUploadItem ->
                            issueUploadService.update(issueUploadItem.issueKey, issueUploadItem.time, issueUploadItem.creator)
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
