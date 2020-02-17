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

        if (Environment.isDevelopmentMode())
            return

        //Today
        def endDate = new Date() + 1
        def startDate = endDate - 2
        jobExecutionService.execute('Download Today\'s Worklogs',
                { SyncJobConfig jobConfig ->
                    worklogDownloadService.getWorklogs(startDate, endDate)
                })

        //Recent
        jobExecutionService.execute('Download Recent Worklogs',
                { SyncJobConfig jobConfig ->
                    worklogDownloadService.getWorklogs(startDate, endDate)
                },
                { SyncJobConfig jobConfig ->
                    endDate = jobConfig.startDate ?: (new Date() + 1)
                    if (endDate < new Date() - 30)
                        endDate = new Date() + 1
                    startDate = endDate - 1
                })

        //Old
        jobExecutionService.execute('Download Old Worklogs',
                { SyncJobConfig jobConfig ->
                    worklogDownloadService.getWorklogs(startDate, endDate)
                },
                { SyncJobConfig jobConfig ->
                    endDate = jobConfig.startDate ?: (new Date() - 30)
                    if (endDate < new Date() - 335)
                        endDate = new Date() - 30
                    startDate = endDate - 1
                })

    }
}
