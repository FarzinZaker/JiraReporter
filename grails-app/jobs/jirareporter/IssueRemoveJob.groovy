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

        def limitDate = new Date()
        use(TimeCategory) {
            limitDate = limitDate - 30.minutes
        }
        Issue.findAllByDeletedDateIsNotNullAndDeletedDateLessThanEqualsAndDeletedCountGreaterThan(limitDate, 0).each {
            issueDownloadService.download(it.key)
        }

        jobExecutionService.execute('Clean Deleted Issues',
                { SyncJobConfig jobConfig, Date startDate, Date endDate, Long lastRecord ->
                    def issues = Issue.createCriteria().list {
                        gt('id', jobConfig.lastRecord)
                        order('id')
                        maxResults(100)
                    }

                    if (issues?.size()) {
                        lastRecord = issues.collect { it.id }.max() as Long
                        issueDownloadService.removeDeleted(issues.collect { it.key })
                    }
                    [
                            startDate : startDate,
                            endDate   : endDate,
                            lastRecord: lastRecord
                    ]
                },
                { SyncJobConfig jobConfig, Date startDate, Date endDate, Long lastRecord ->
                    use(TimeCategory) {
                        endDate = new Date() - 1.day
                    }
                    lastRecord = 0
                    [
                            startDate : startDate,
                            endDate   : endDate,
                            lastRecord: lastRecord
                    ]
                },
                { SyncJobConfig jobConfig, Date startDate, Date endDate, Long lastRecord ->
                    jobConfig.lastRecord = lastRecord
                    [
                            startDate : startDate,
                            endDate   : endDate,
                            lastRecord: lastRecord
                    ]
                })
    }

}
