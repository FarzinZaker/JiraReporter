package jirareporter

class GitHubRepositoriesSyncJob {
    static triggers = {
        simple repeatInterval: 10 * 60 * 1000l // execute job once in 5 seconds
    }

    static concurrent = false

    def jobExecutionService
    def gitHubService

    def execute() {

        if (!jobExecutionService.jobsEnabled())
            return

        jobExecutionService.execute('Sync GitHub Repositories',
                { SyncJobConfig jobConfig, Date startDate, Date endDate, Long lastRecord ->
                    if (!lastRecord)
                        lastRecord = 1
                    def count = gitHubService.downloadRepositories(lastRecord++)
                    if (count < 1) {
                        println "All Repositories Synced"
                        lastRecord = 1
                    }
                    [
                            startDate : startDate,
                            endDate   : endDate,
                            lastRecord: lastRecord
                    ]
                }

        )
    }
}
