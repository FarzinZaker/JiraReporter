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

        if (!jobExecutionService.jobsEnabled())
            return

        //Recent
        jobExecutionService.execute('Download Recent CrossOver Logs',
                { SyncJobConfig jobConfig, Date startDate, Date endDate, Long lastRecord ->
                    CrossOverLog.withTransaction {
                        crossOverService.persist(startDate, endDate, Team.findAllByDeleted(false), true)
                    }
                    [
                            startDate: startDate,
                            endDate: endDate,
                            lastRecord: lastRecord
                    ]
                },
                { SyncJobConfig jobConfig, Date startDate, Date endDate, Long lastRecord ->
                    endDate = jobConfig.startDate ?: (new Date() + 1)
                    if (endDate < new Date() - 7)
                        endDate = new Date() + 1
                    startDate = endDate - 7
                    [
                            startDate: startDate,
                            endDate: endDate,
                            lastRecord: lastRecord
                    ]
                },
                { SyncJobConfig jobConfig, Date startDate, Date endDate, Long lastRecord ->
                    jobConfig.startDate = startDate
                    jobConfig.endDate = endDate
                    [
                            startDate: startDate,
                            endDate: endDate,
                            lastRecord: lastRecord
                    ]
                })

        //Old
        jobExecutionService.execute('Download Old CrossOver Logs',
                { SyncJobConfig jobConfig, Date startDate, Date endDate, Long lastRecord ->
                    CrossOverLog.withTransaction {
                        crossOverService.persist(startDate, endDate, Team.findAllByDeleted(false), true)
                    }
                    [
                            startDate: startDate,
                            endDate: endDate,
                            lastRecord: lastRecord
                    ]
                },
                { SyncJobConfig jobConfig, Date startDate, Date endDate, Long lastRecord ->
                    endDate = jobConfig.startDate ?: (new Date() + 1)
                    if (endDate < new Date() - 365)
                        endDate = new Date() - 7
                    startDate = endDate - 7
                    [
                            startDate: startDate,
                            endDate: endDate,
                            lastRecord: lastRecord
                    ]
                },
                { SyncJobConfig jobConfig, Date startDate, Date endDate, Long lastRecord ->
                    jobConfig.startDate = startDate
                    jobConfig.endDate = endDate
                    [
                            startDate: startDate,
                            endDate: endDate,
                            lastRecord: lastRecord
                    ]
                })
    }
}
