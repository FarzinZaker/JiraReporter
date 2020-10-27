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
        def lst = Issue.createCriteria().list{
            eq('deleted', false)
            isNotNull('deletedDate')
            lte('deletedDate', limitDate)
            gt('deletedCount', 0)
            projections{
                property('key')
            }
        }
        lst.each {
            issueDownloadService.enqueue(it, "Remove Job")
        }

        jobExecutionService.execute('Clean Deleted Issues',
                { SyncJobConfig jobConfig, Date startDate, Date endDate, Long lastRecord ->
                    def issues = Issue.createCriteria().list {
                        eq('deleted', false)
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
