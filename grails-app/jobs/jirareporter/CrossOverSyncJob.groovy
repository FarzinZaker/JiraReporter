package jirareporter

class CrossOverSyncJob {

    static triggers = {
//        simple repeatInterval: 5000l // execute job once in 5 seconds
    }

    static concurrent = false

    def syncService


    def execute() {
        def jobConfig = SyncJobConfig.findByName('OLD_ISSUES')
        if (!jobConfig)
            jobConfig = new SyncJobConfig(name: 'OLD_ISSUES').save(flush: true)

        def endDate = jobConfig.startDate ?: (new Date() - 30)
        if (endDate < new Date() - 335)
            endDate = new Date() - 30
        def startDate = endDate - 2

        syncService.getWorklogs(startDate, endDate)

        jobConfig = SyncJobConfig.findByName('OLD_ISSUES')
        jobConfig.startDate = startDate
        jobConfig.endDate = endDate
        jobConfig.save(flush: true)
    }
}
