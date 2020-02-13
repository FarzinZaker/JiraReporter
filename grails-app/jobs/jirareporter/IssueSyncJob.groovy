package jirareporter

import grails.util.Environment
import groovy.time.TimeCategory

class IssueSyncJob {
    static triggers = {
        simple repeatInterval: 5 * 60 * 1000l // execute job once in 5 seconds
    }

    static concurrent = false

    def issueDownloadService

    def execute() {

        if (Environment.isDevelopmentMode())
            return

        //Updated
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

        jobConfig = SyncJobConfig.findByName('RECENT_ISSUES')
        if (!jobConfig)
            jobConfig = new SyncJobConfig(name: 'RECENT_ISSUES').save(flush: true)

        endDate = jobConfig.startDate ?: new Date()
        if (endDate < new Date() - 90)
            endDate = new Date()
        def startDate = endDate - 1

        try {
            issueDownloadService.queueIssues(startDate, endDate)

            jobConfig = SyncJobConfig.findByName('RECENT_ISSUES')
            jobConfig.startDate = startDate
            jobConfig.endDate = endDate
            jobConfig.save(flush: true)
        } catch (Exception ex) {
            println ex.message
            throw ex
        }

        jobConfig = SyncJobConfig.findByName('OLD_ISSUES')
        if (!jobConfig)
            jobConfig = new SyncJobConfig(name: 'OLD_ISSUES').save(flush: true)

        endDate = jobConfig.startDate ?: new Date()
        if (endDate < new Date() - 365)
            endDate = new Date()
        startDate = endDate - 1

        try {
            issueDownloadService.queueIssues(startDate, endDate)

            jobConfig = SyncJobConfig.findByName('OLD_ISSUES')
            jobConfig.startDate = startDate
            jobConfig.endDate = endDate
            jobConfig.save(flush: true)
        } catch (Exception ex) {
            println ex.message
            throw ex
        }
    }
}
