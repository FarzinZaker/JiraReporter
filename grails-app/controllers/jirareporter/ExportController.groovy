package jirareporter

import grails.plugin.springsecurity.annotation.Secured
import groovy.time.TimeCategory

@Secured([Roles.ADMIN, Roles.MANAGER, Roles.JIRA_USER])
class ExportController {

    def exportService
    def filterService
    def reportService
    def crossOverService
    def accomplishmentsService

    def tasks() {
        exportService.exportTasks(tasks, response)
    }

    def worklogs() {
        exportService.exportWorklogs(worklogs, response)
    }

    private List<Worklog> getWorklogs() {
        if (!params.from)
            return redirect(action: 'report', params: params + [from: (new Date() - 1).format('MM/dd/yyyy')])
        if (!params.to)
            return redirect(action: 'report', params: params + [to: new Date().format('MM/dd/yyyy')])

        Date from = filterService.formatFromDate(params)
        Date to = filterService.formatToDate(params)
        if (to)
            use(TimeCategory) {
                to = to - 1.second
            }

        def teams = filterService.formatTeams(params)
        def crossOverLogs = crossOverService.getWorkingHours(from, to, teams)

        reportService.getWorklogs(
                from,
                to,
                filterService.formatProjects(params),
                filterService.formatIssueTypes(params),
                filterService.formatPriorities(params),
                filterService.formatComponents(params),
                filterService.formatClients(params),
                filterService.formatLabels(params),
                filterService.formatUsersList(params),
                filterService.formatTeamMembers(crossOverLogs.keySet() as Set<String>),
                teams?.size > 0,
                filterService.formatWorklogTypes(params),
                filterService.formatStatus(params))
    }

    private List<Issue> getTasks() {
        accomplishmentsService.getTasks(getWorklogs())
    }
}
