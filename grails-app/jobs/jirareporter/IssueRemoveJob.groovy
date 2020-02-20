package jirareporter

import grails.util.Environment
import groovy.time.TimeCategory

class IssueRemoveJob {
    static triggers = {
        simple repeatInterval: 1000l // execute job once in 5 seconds
    }

    static concurrent = false

    def issueDownloadService
    def jobExecutionService

    def execute() {

        if (!jobExecutionService.jobsEnabled())
            return

        def endDate
        def lastRecord = 0
        jobExecutionService.execute('Clean Deleted Issues',
                { SyncJobConfig jobConfig ->
                    def issues = Issue.createCriteria().list {
                        gt('id', jobConfig.lastRecord)
                        order('id')
                        maxResults(100)
                    }

                    if (issues?.size()) {
                        lastRecord = issues.collect { it.id }.max() as Long
                        issueDownloadService.removeDeleted(issues.collect { it.key })
                    }
                },
                { SyncJobConfig jobConfig ->
                    use(TimeCategory) {
                        endDate = new Date() - 1.day
                    }
                    jobConfig.lastRecord = 0
                    jobConfig.save(flush: true)
                },
                { SyncJobConfig jobConfig ->
                    jobConfig.lastRecord = lastRecord
                })
    }

}
