package jirareporter

import grails.util.Environment

class WorklogsSyncJob {
    static triggers = {
        simple repeatInterval: 5000l // execute job once in 5 seconds
    }

    static concurrent = false

    def syncService

    def execute() {

        if(!Environment.isDevelopmentMode())
            return

        //Recent
        def jobConfig = SyncJobConfig.findByName('RECENT_ISSUES')
        if (!jobConfig)
            jobConfig = new SyncJobConfig(name: 'RECENT_ISSUES').save(flush: true)

        def endDate = jobConfig.startDate ?: (new Date() + 1)
        if (endDate < new Date() - 30)
            endDate = new Date() + 1
        def startDate = endDate - 2

        syncService.getWorklogs(startDate, endDate)

        jobConfig = SyncJobConfig.findByName('RECENT_ISSUES')
        jobConfig.startDate = startDate
        jobConfig.endDate = endDate
        jobConfig.save(flush: true)

        //Old
        jobConfig = SyncJobConfig.findByName('OLD_ISSUES')
        if (!jobConfig)
            jobConfig = new SyncJobConfig(name: 'OLD_ISSUES').save(flush: true)

        endDate = jobConfig.startDate ?: (new Date() - 30)
        if (endDate < new Date() - 335)
            endDate = new Date() - 30
        startDate = endDate - 2

        syncService.getWorklogs(startDate, endDate)

        jobConfig = SyncJobConfig.findByName('OLD_ISSUES')
        jobConfig.startDate = startDate
        jobConfig.endDate = endDate
        jobConfig.save(flush: true)
    }
}
