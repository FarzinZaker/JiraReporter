package jirareporter

import grails.util.Environment
import groovy.time.TimeCategory

class IssueSyncJob {
    static triggers = {
        simple repeatInterval: 1 * 60 * 1000l // execute job once in 5 seconds
    }

    static concurrent = false

    def issueDownloadService

    def execute() {

        if (Environment.isDevelopmentMode())
            return

        //Recent
        def endDate = null
        def jobConfig = SyncJobConfig.findByName('UPDATED_ISSUES')
        if (!jobConfig) {
            use(TimeCategory) {
                endDate = new Date() - 1.day
            }
            jobConfig = new SyncJobConfig(name: 'UPDATED_ISSUES', startDate: endDate, endDate: endDate).save(flush: true)
        }
        endDate = jobConfig.endDate
        def newEndDate = new Date()

        try {
            issueDownloadService.queueIssues(endDate)

            jobConfig = SyncJobConfig.findByName('UPDATED_ISSUES')
            jobConfig.startDate = newEndDate
            jobConfig.endDate = newEndDate
            jobConfig.save(flush: true)
        } catch (Exception ex) {
            println ex.message
            throw ex
        }
    }
}
