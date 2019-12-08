package jirareporter

import grails.util.Environment

class IssueFixJob {
    static triggers = {
        simple repeatInterval: 1000l // execute job once in 5 seconds
    }

    static concurrent = false

    def issueFixService
    def filterService

    def execute() {

        if(!Environment.isDevelopmentMode())
            return

        def jobConfig = SyncJobConfig.findByName('FIXED_ISSUES')
        if (!jobConfig)
            jobConfig = new SyncJobConfig(name: 'FIXED_ISSUES').save(flush: true)

        def endDate = jobConfig.startDate ?: new Date()
        if (endDate < new Date() - 335) {
            endDate = new Date()
//            println 'FIX is DONE'
        }
        def startDate = endDate - 2

        def statusList = filterService.formatStatus([status: ['Draft', 'To Do', 'In Progress'].join(',')])
        def users = JiraUser.findAllByTeamNameInList(Configuration.crossOverTeams.collect { it.name } ?: [null])
        Issue.createCriteria().list {
//            'in'('status', statusList)
            'in'('assignee', users)
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
