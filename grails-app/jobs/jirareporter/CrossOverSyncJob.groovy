package jirareporter

import grails.util.Environment

class CrossOverSyncJob {

    static triggers = {
        simple repeatInterval: 1 * 60 * 1000l // execute job once in 5 seconds
    }

    static concurrent = false

    def crossOverService


    def execute() {

        if (!Environment.isDevelopmentMode())
            return

        //Recent
        def jobConfig = SyncJobConfig.findByName('RECENT_XO_LOGS')
        if (!jobConfig)
            jobConfig = new SyncJobConfig(name: 'RECENT_XO_LOGS').save(flush: true)

        def endDate = jobConfig.startDate ?: (new Date() + 1)
        if (endDate < new Date() - 7)
            endDate = new Date() + 1
        def startDate = endDate - 7

        crossOverService.persist(startDate, endDate, Team.list(), true)

        jobConfig = SyncJobConfig.findByName('RECENT_XO_LOGS')
        jobConfig.startDate = startDate
        jobConfig.endDate = endDate
        jobConfig.save(flush: true)

        //Old
        jobConfig = SyncJobConfig.findByName('OLD_XO_LOGS')
        if (!jobConfig)
            jobConfig = new SyncJobConfig(name: 'OLD_XO_LOGS').save(flush: true)

        endDate = jobConfig.startDate ?: (new Date() - 7)
        if (endDate < new Date() - 335)
            endDate = new Date() - 7
        startDate = endDate - 7

        crossOverService.persist(startDate, endDate, Team.list())

        jobConfig = SyncJobConfig.findByName('OLD_XO_LOGS')
        jobConfig.startDate = startDate
        jobConfig.endDate = endDate
        jobConfig.save(flush: true)
    }
}
