package jirareporter

import grails.util.Environment
import groovy.time.TimeCategory

class IssueFixJob {
    static triggers = {
        simple repeatInterval: 1000l // execute job once in 5 seconds
    }

    static concurrent = false

    def issueFixService
    def jobExecutionService

    def execute() {

        if (Environment.isDevelopmentMode())
            return

        jobExecutionService.execute('Fill Issues Missing Information',
                { SyncJobConfig jobConfig ->
                    def minSyncDate = new Date()
                    use(TimeCategory) {
                        minSyncDate = minSyncDate - 2.hours
                    }
                    def downloadQueue = IssueDownloadItem.list().collect { it.issueKey } ?: ['-']
                    def users = JiraUser.findAllByTeamInList(Team.list()) ?: [null]
                    try {
                        def list = Issue.createCriteria().list {
                            'in'('assignee', users)
                            gte('lastSync', minSyncDate)
                            isNull('lastFix')
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
                        }
                    } catch (ex) {
                        println ex
                        throw ex
                    }
                })
    }
}
