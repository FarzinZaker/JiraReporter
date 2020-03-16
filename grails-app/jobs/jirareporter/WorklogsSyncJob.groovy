package jirareporter


import grails.util.Environment

class WorklogsSyncJob {
    static triggers = {
        simple repeatInterval: 5 * 60 * 1000l // execute job once in 5 seconds
    }

    static concurrent = false

    def worklogDownloadService
    def jobExecutionService

    def execute() {

        if (!jobExecutionService.jobsEnabled())
            return

        //Today
        jobExecutionService.execute('Download Today\'s Worklogs',
                { SyncJobConfig jobConfig, Date startDate, Date endDate, Long lastRecord ->
                    worklogDownloadService.getWorklogs(startDate, endDate)
                    [
                            startDate: startDate,
                            endDate: endDate,
                            lastRecord: lastRecord
                    ]
                },
                { SyncJobConfig jobConfig, Date startDate, Date endDate, Long lastRecord ->
                    endDate = new Date() + 1
                    startDate = endDate - 2
                    [
                            startDate: startDate,
                            endDate: endDate,
                            lastRecord: lastRecord
                    ]
                })

        //Recent
        jobExecutionService.execute('Download Recent Worklogs',
                { SyncJobConfig jobConfig, Date startDate, Date endDate, Long lastRecord ->
                    worklogDownloadService.getWorklogs(startDate, endDate)
                    [
                            startDate: startDate,
                            endDate: endDate,
                            lastRecord: lastRecord
                    ]
                },
                { SyncJobConfig jobConfig, Date startDate, Date endDate, Long lastRecord ->
                    endDate = jobConfig.startDate ?: (new Date() + 1)
                    if (endDate < new Date() - 30)
                        endDate = new Date() + 1
                    startDate = endDate - 1
                    [
                            startDate: startDate,
                            endDate: endDate,
                            lastRecord: lastRecord
                    ]
                })

        //Old
        jobExecutionService.execute('Download Old Worklogs',
                { SyncJobConfig jobConfig, Date startDate, Date endDate, Long lastRecord ->
                    worklogDownloadService.getWorklogs(startDate, endDate)
                    [
                            startDate: startDate,
                            endDate: endDate,
                            lastRecord: lastRecord
                    ]
                },
                { SyncJobConfig jobConfig, Date startDate, Date endDate, Long lastRecord ->
                    endDate = jobConfig.startDate ?: (new Date() - 30)
                    if (endDate < new Date() - 335)
                        endDate = new Date() - 30
                    startDate = endDate - 1
                    [
                            startDate: startDate,
                            endDate: endDate,
                            lastRecord: lastRecord
                    ]
                })

    }
}
