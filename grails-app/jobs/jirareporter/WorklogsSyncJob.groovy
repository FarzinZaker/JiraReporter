package jirareporter


import grails.util.Environment

class WorklogsSyncJob {
    static triggers = {
        simple repeatInterval: 5000l // execute job once in 5 seconds
    }

    static concurrent = false

    def downloadService
    def issueDownloadService

    def execute() {

        if(!Environment.isDevelopmentMode())
            return

//        if (IssueDownloadItem.count() > 0) {
        def issueDownloadItems = IssueDownloadItem.findAllByIdGreaterThan(0, [max: 100])
//            def threads = []
        issueDownloadItems.each { issueDownloadItem ->
//                threads << Thread.start {
//                    try {
//                        Issue.withNewTransaction {
            issueDownloadService.download(issueDownloadItem.issue.key)
            IssueDownloadItem.executeUpdate("delete IssueDownloadItem where issue = :issue", [issue: issueDownloadItem.issue])
//                        }
//                    } catch (ex) {
//                        println ex.message
//                    }
//                }
//            }
//            threads.each { it.join() }
        }

        //Today
        def endDate = new Date() + 1
        def startDate = endDate - 2

        try {
            downloadService.getWorklogs(startDate, endDate)
        } catch (Exception ex) {
            println ex.message
        }

        //Recent
        def jobConfig = SyncJobConfig.findByName('RECENT_ISSUES')
        if (!jobConfig)
            jobConfig = new SyncJobConfig(name: 'RECENT_ISSUES').save(flush: true)

        endDate = jobConfig.startDate ?: (new Date() + 1)
        if (endDate < new Date() - 30)
            endDate = new Date() + 1
        startDate = endDate - 1

        try {
            downloadService.getWorklogs(startDate, endDate)

            jobConfig = SyncJobConfig.findByName('RECENT_ISSUES')
            jobConfig.startDate = startDate
            jobConfig.endDate = endDate
            jobConfig.save(flush: true)
        } catch (Exception ex) {
            println ex.message
        }

        //Old
        jobConfig = SyncJobConfig.findByName('OLD_ISSUES')
        if (!jobConfig)
            jobConfig = new SyncJobConfig(name: 'OLD_ISSUES').save(flush: true)

        endDate = jobConfig.startDate ?: (new Date() - 30)
        if (endDate < new Date() - 335)
            endDate = new Date() - 30
        startDate = endDate - 1

        try {
            downloadService.getWorklogs(startDate, endDate)

            jobConfig = SyncJobConfig.findByName('OLD_ISSUES')
            jobConfig.startDate = startDate
            jobConfig.endDate = endDate
            jobConfig.save(flush: true)
        } catch (Exception ex) {
            println ex.message
        }
    }
}
