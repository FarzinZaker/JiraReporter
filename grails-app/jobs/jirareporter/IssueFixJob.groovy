package jirareporter

class IssueFixJob {
    static triggers = {
        simple repeatInterval: 1000l // execute job once in 5 seconds
    }

    def issueFixService

    def execute() {

        def jobConfig = SyncJobConfig.findByName('FIXED_ISSUES')
        if (!jobConfig)
            jobConfig = new SyncJobConfig(name: 'FIXED_ISSUES').save(flush: true)

        def endDate = jobConfig.startDate ?: new Date()
        if (endDate < new Date() - 335)
            endDate = new Date()
        def startDate = endDate - 2

        Issue.createCriteria().list {
            between('updated', startDate, endDate)
        }?.each { Issue issue ->
            issueFixService.fix(issue)
        }

        jobConfig = SyncJobConfig.findByName('FIXED_ISSUES')
        jobConfig.startDate = startDate
        jobConfig.endDate = endDate
        jobConfig.save(flush: true)
    }
}
