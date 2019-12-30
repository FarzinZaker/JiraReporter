package jirareporter


import grails.util.Environment

class WorklogsSyncJob {
    static triggers = {
        simple repeatInterval: 5 * 60 * 1000l // execute job once in 5 seconds
    }

    static concurrent = false

    def worklogDownloadService

    def execute() {

        if (!Environment.isDevelopmentMode())
            return

        Date timer = new Date()

        //Today
        def endDate = new Date() + 1
        def startDate = endDate - 2

        try {
            worklogDownloadService.getWorklogs(startDate, endDate)
        } catch (Exception ex) {
            println ex.message
            throw ex
        }

//        println('TODAY:\t' + (new Date().time - timer.time))

        timer = new Date()
//        if (timer.hours < 1 || timer.hours > 6)
//            return

        //Recent
        def jobConfig = SyncJobConfig.findByName('RECENT_WORKLOGS')
        if (!jobConfig)
            jobConfig = new SyncJobConfig(name: 'RECENT_WORKLOGS').save(flush: true)

        endDate = jobConfig.startDate ?: (new Date() + 1)
        if (endDate < new Date() - 30)
            endDate = new Date() + 1
        startDate = endDate - 1

        try {
            worklogDownloadService.getWorklogs(startDate, endDate)

            jobConfig = SyncJobConfig.findByName('RECENT_WORKLOGS')
            jobConfig.startDate = startDate
            jobConfig.endDate = endDate
            jobConfig.save(flush: true)
        } catch (Exception ex) {
            println ex.message
            throw ex
        }

//        println('RECENT:\t' + (new Date().time - timer.time))
        timer = new Date()

        //Old
        jobConfig = SyncJobConfig.findByName('OLD_WORKLOGS')
        if (!jobConfig)
            jobConfig = new SyncJobConfig(name: 'OLD_WORKLOGS').save(flush: true)

        endDate = jobConfig.startDate ?: (new Date() - 30)
        if (endDate < new Date() - 335)
            endDate = new Date() - 30
        startDate = endDate - 1

        try {
            worklogDownloadService.getWorklogs(startDate, endDate)

            jobConfig = SyncJobConfig.findByName('OLD_WORKLOGS')
            jobConfig.startDate = startDate
            jobConfig.endDate = endDate
            jobConfig.save(flush: true)
        } catch (Exception ex) {
            println ex.message
            throw ex
        }

//        println('OLD:\t' + (new Date().time - timer.time))
    }
}
