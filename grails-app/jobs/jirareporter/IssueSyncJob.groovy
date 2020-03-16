package jirareporter

import grails.util.Environment
import groovy.time.TimeCategory

class IssueSyncJob {
    static triggers = {
        simple repeatInterval: 5 * 60 * 1000l // execute job once in 5 seconds
    }

    static concurrent = false

    def issueDownloadService
    def jobExecutionService

    def execute() {

        if (!jobExecutionService.jobsEnabled())
            return

        //Updated
        jobExecutionService.execute('Download Issues Updated Today',
                { SyncJobConfig jobConfig, Date startDate, Date endDate, Long lastRecord ->
                    issueDownloadService.queueIssues(endDate)
                    [
                            startDate: startDate,
                            endDate: endDate,
                            lastRecord: lastRecord
                    ]
                },
                { SyncJobConfig jobConfig, Date startDate, Date endDate, Long lastRecord ->
                    endDate = new Date()
                    [
                            startDate: startDate,
                            endDate: endDate,
                            lastRecord: lastRecord
                    ]
                },
                { SyncJobConfig jobConfig, Date startDate, Date endDate, Long lastRecord ->
                    jobConfig.startDate = endDate
                    jobConfig.endDate = endDate
                    [
                            startDate: startDate,
                            endDate: endDate,
                            lastRecord: lastRecord
                    ]
                })


        //Recent
        jobExecutionService.execute('Download Recent Issues',
                { SyncJobConfig jobConfig, Date startDate, Date endDate, Long lastRecord ->
                    issueDownloadService.queueIssues(startDate, endDate)
                    [
                            startDate: startDate,
                            endDate: endDate,
                            lastRecord: lastRecord
                    ]
                },
                { SyncJobConfig jobConfig, Date startDate, Date endDate, Long lastRecord ->
                    endDate = jobConfig.startDate ?: new Date()
                    if (endDate < new Date() - 90)
                        endDate = new Date()
                    startDate = endDate - 1
                    [
                            startDate: startDate,
                            endDate: endDate,
                            lastRecord: lastRecord
                    ]
                },
                { SyncJobConfig jobConfig, Date startDate, Date endDate, Long lastRecord ->
                    jobConfig.startDate = endDate
                    jobConfig.endDate = endDate
                    [
                            startDate: startDate,
                            endDate: endDate,
                            lastRecord: lastRecord
                    ]
                })

        //Old
        jobExecutionService.execute('Download Old Issues',
                { SyncJobConfig jobConfig, Date startDate, Date endDate, Long lastRecord ->
                    issueDownloadService.queueIssues(startDate, endDate)
                    [
                            startDate: startDate,
                            endDate: endDate,
                            lastRecord: lastRecord
                    ]
                },
                { SyncJobConfig jobConfig, Date startDate, Date endDate, Long lastRecord ->
                    endDate = jobConfig.startDate ?: new Date()
                    if (endDate < new Date() - 365)
                        endDate = new Date()
                    startDate = endDate - 1
                    [
                            startDate: startDate,
                            endDate: endDate,
                            lastRecord: lastRecord
                    ]
                },
                { SyncJobConfig jobConfig, Date startDate, Date endDate, Long lastRecord ->
                    jobConfig.startDate = endDate
                    jobConfig.endDate = endDate
                    [
                            startDate: startDate,
                            endDate: endDate,
                            lastRecord: lastRecord
                    ]
                })
    }
}
