package jirareporter

import grails.util.Environment

class CrossOverSyncJob {

    static triggers = {
        simple repeatInterval: 1 * 60 * 1000l // execute job once in 5 seconds
    }

    static concurrent = false

    def jobExecutionService
    def crossOverService


    def execute() {

        if (Environment.isDevelopmentMode())
            return

        //Recent
        def startDate
        def endDate
        jobExecutionService.execute('Download Recent CrossOver Logs',
                { SyncJobConfig jobConfig ->
                    crossOverService.persist(startDate, endDate, Team.list(), true)
                },
                { SyncJobConfig jobConfig ->
                    endDate = jobConfig.startDate ?: (new Date() + 1)
                    if (endDate < new Date() - 7)
                        endDate = new Date() + 1
                    startDate = endDate - 7
                },
                { SyncJobConfig jobConfig ->
                    jobConfig.startDate = startDate
                    jobConfig.endDate = endDate
                })

        //Old
        jobExecutionService.execute('Download Old CrossOver Logs',
                { SyncJobConfig jobConfig ->
                    crossOverService.persist(startDate, endDate, Team.list(), true)
                },
                { SyncJobConfig jobConfig ->
                    endDate = jobConfig.startDate ?: (new Date() + 1)
                    if (endDate < new Date() - 365)
                        endDate = new Date() - 7
                    startDate = endDate - 7
                },
                { SyncJobConfig jobConfig ->
                    jobConfig.startDate = startDate
                    jobConfig.endDate = endDate
                })
    }
}
