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
                { SyncJobConfig jobConfig, Date startDate, Date endDate, Long lastRecord ->
                    IssueDownloadItem.withTransaction {
                        def issueDownloadItems = IssueDownloadItem.findAllBySourceInList(['MANUAL', 'User'], [max: 100])
                        issueDownloadItems.each { issueDownloadItem ->
                            issueDownloadService.download(issueDownloadItem.issueKey)
                            issueDownloadItem.delete()
                        }
                    }
                    [
                            startDate: startDate,
                            endDate: endDate,
                            lastRecord: lastRecord
                    ]
                })

        jobExecutionService.execute('Download All Queued Issues',
                { SyncJobConfig jobConfig, Date startDate, Date endDate, Long lastRecord ->
                    def issueDownloadItems
                    IssueDownloadItem.withTransaction {
                        issueDownloadItems = IssueDownloadItem.createCriteria().list{
                            projections {
                                property('issueKey')
                                maxResults(10)
                            }
                        }
                    }
                    issueDownloadItems?.each { key ->
                        IssueDownloadItem.withNewTransaction { transaction ->
                            issueDownloadService.download(key)
                            IssueDownloadItem.findByIssueKey(key).delete()
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
