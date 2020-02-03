package jirareporter

import grails.plugin.springsecurity.annotation.Secured
import groovy.time.TimeCategory

@Secured([Roles.ADMIN, Roles.MANAGER, Roles.JIRA_USER])
class WorklogController {

    def reportService
    def refinementService
    def componentService
    def integrityService
    def accomplishmentsService
    def crossOverService
    def filterService

    def report() {

        def components = componentService.getAll(Configuration.projects.collect { it.key?.toString() })
        def clients = Client.list()

        if (!params.from)
            return redirect(action: 'report', params: params + [from: (new Date() - 1).format('MM/dd/yyyy')])
        if (!params.to)
            return redirect(action: 'report', params: params + [to: new Date().format('MM/dd/yyyy')])

        Date from = filterService.formatFromDate(params)
        Date to = filterService.formatToDate(params)
        use(TimeCategory) {
            to = to - 1.second
        }

        def teams = filterService.formatTeams(params)
        def crossOverLogs = crossOverService.getWorkingHours(from, to, teams)

        def worklogs = reportService.getWorklogs(
                from,
                to,
                filterService.formatProjects(params),
                filterService.formatIssueTypes(params),
                filterService.formatPriorities(params),
                filterService.formatComponents(params),
                filterService.formatClients(params),
                filterService.formatUsersList(params),
                filterService.formatTeamMembers(crossOverLogs.keySet() as Set<String>),
                teams?.size > 0,
                filterService.formatWorklogTypes(params),
                filterService.formatStatus(params))

        def clientDetails = refinementService.getClientDetails(worklogs)
        def componentDetails = refinementService.getComponentDetails(worklogs)
        def issueTypeDetails = refinementService.getIssueTypeDetails(worklogs)
        def projectDetails = refinementService.getProjectDetails(worklogs)

        def userSummary = refinementService.getDeveloperSummary(worklogs)
        def clientSummary = refinementService.getClientSummary(worklogs)
        def componentSummary = refinementService.getComponentSummary(worklogs)
        def issueTypeSummary = refinementService.getIssueTypeSummary(worklogs)
        def projectSummary = refinementService.getProjectSummary(worklogs)

        def integritySummary = integrityService.getDeveloperIntegritySummary(worklogs, from, to, crossOverLogs)
        def accomplishments = accomplishmentsService.getTasks(worklogs)

        [
                components      : components,
                clients         : clients,
                worklogs        : worklogs,

                userSummary     : userSummary,
                clientSummary   : clientSummary,
                componentSummary: componentSummary,
                issueTypeSummary: issueTypeSummary,
                projectSummary  : projectSummary,

                clientDetails   : clientDetails,
                componentDetails: componentDetails,
                issueTypeDetails: issueTypeDetails,
                projectDetails  : projectDetails,

                integritySummary: integritySummary,

                accomplishments : accomplishments
        ]
    }
}
