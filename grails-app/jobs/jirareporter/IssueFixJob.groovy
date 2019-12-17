package jirareporter

import grails.util.Environment
import groovy.time.TimeCategory

class IssueFixJob {
    static triggers = {
        simple repeatInterval: 1000l // execute job once in 5 seconds
    }

    static concurrent = false

    def issueFixService
    def filterService

    def execute() {

        if (!Environment.isDevelopmentMode())
            return

//        def jobConfig = SyncJobConfig.findByName('FIXED_ISSUES')
//        if (!jobConfig)
//            jobConfig = new SyncJobConfig(name: 'FIXED_ISSUES').save(flush: true)
//
//        def endDate = jobConfig.startDate ?: new Date()
//        if (endDate < new Date() - 335) {
//            endDate = new Date()
////            println 'FIX is DONE'
//        }
//        def startDate = endDate - 2

//        def statusList = filterService.formatStatus([status: ['Draft', 'To Do', 'In Progress'].join(',')])
//        def lastFixDate = new Date()
//        use(TimeCategory) {
//            lastFixDate = lastFixDate - 1.hour
//        }
        def downloadQueue = IssueDownloadItem.list().collect { it.issueKey } ?: ['-']
        def users = JiraUser.findAllByTeamInList(Team.list()) ?: [null]
        try {
            def list = Issue.createCriteria().list {
//            'in'('status', statusList)
                'in'('assignee', users)
//            between('updated', startDate, endDate)
//            lt('lastFix', lastFixDate)
                not {
                    'in'('key', downloadQueue)
                }
                or {
                    isNull('originalEstimate')
                    eq('originalEstimate', '')
                    lt('originalEstimateSeconds', 1l)
                    isNull('remainingEstimate')
                    eq('remainingEstimate', '')
                    isNull('startDate')
                    isNull('dueDate')
                    ltProperty('dueDate', 'startDate')
                    eqProperty('dueDate', 'startDate')
                }
                maxResults(20)
            }
            list?.each { Issue issue ->
                issueFixService.fix(issue)
//            println issue.key
            }
        } catch (ex) {
            println ex
            throw ex
        }

//        jobConfig = SyncJobConfig.findByName('FIXED_ISSUES')
//        jobConfig.startDate = startDate
//        jobConfig.endDate = endDate
//        jobConfig.save(flush: true)

    }
}
