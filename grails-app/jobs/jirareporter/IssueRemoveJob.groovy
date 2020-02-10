package jirareporter

import grails.util.Environment
import groovy.time.TimeCategory

class IssueRemoveJob {
    static triggers = {
        simple repeatInterval: 1000l // execute job once in 5 seconds
    }

    static concurrent = false

    def issueDownloadService

    def execute() {

        if (Environment.isDevelopmentMode())
            return

        def endDate = null
        def jobConfig = SyncJobConfig.findByName('DELETED_ISSUES')
        if (!jobConfig) {
            use(TimeCategory) {
                endDate = new Date() - 1.day
            }
            jobConfig = new SyncJobConfig(name: 'DELETED_ISSUES', lastRecord: 0).save(flush: true)
        }


        try {
            def issues = Issue.createCriteria().list {
                gt('id', jobConfig.lastRecord)
                order('id')
                maxResults(100)
            }

            def lastRecord = 0
            if (issues?.size()) {
                lastRecord = issues.collect { it.id }.max() as Long
                issueDownloadService.removeDeleted(issues.collect { it.key })
            }

            jobConfig = SyncJobConfig.findByName('DELETED_ISSUES')
            jobConfig.lastRecord = lastRecord
            jobConfig.save(flush: true)
        } catch (Exception ex) {
            println ex.message
            throw ex
        }
    }

}
