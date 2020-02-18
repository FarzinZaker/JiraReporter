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

        if (!Environment.isDevelopmentMode())
            return

        //Updated
        def endDate = null
        jobExecutionService.execute('Download Issues Updated Today',
                { SyncJobConfig jobConfig ->
                    issueDownloadService.queueIssues(endDate)
                },
                { SyncJobConfig jobConfig ->
                    endDate = new Date()
                },
                { SyncJobConfig jobConfig ->
                    jobConfig.startDate = endDate
                    jobConfig.endDate = endDate
                })


        //Recent
        def startDate = null
        endDate = null
        jobExecutionService.execute('Download Recent Issues',
                { SyncJobConfig jobConfig ->
                    issueDownloadService.queueIssues(startDate, endDate)
                },
                { SyncJobConfig jobConfig ->
                    endDate = jobConfig.startDate ?: new Date()
                    if (endDate < new Date() - 90)
                        endDate = new Date()
                    startDate = endDate - 1
                },
                { SyncJobConfig jobConfig ->
                    jobConfig.startDate = endDate
                    jobConfig.endDate = endDate
                })

        //Old
        startDate = null
        endDate = null
        jobExecutionService.execute('Download Old Issues',
                { SyncJobConfig jobConfig ->
                    issueDownloadService.queueIssues(startDate, endDate)
                },
                { SyncJobConfig jobConfig ->
                    endDate = jobConfig.startDate ?: new Date()
                    if (endDate < new Date() - 365)
                        endDate = new Date()
                    startDate = endDate - 1
                },
                { SyncJobConfig jobConfig ->
                    jobConfig.startDate = endDate
                    jobConfig.endDate = endDate
                })
    }
}
