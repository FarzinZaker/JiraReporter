package jirareporter

import groovy.time.TimeCategory

class WorklogController {

    def reportService
    def refinementService
    def componentService
    def integrityService
    def accomplishmentsService
    def crossOverService

    def report() {

        println params

        def components = componentService.getAll(Configuration.projects.collect { it.key?.toString() })

        if (!params.from)
            return redirect(action: 'report', params: params + [from: (new Date() - 1).format('MM/dd/yyyy')])
        if (!params.to)
            return redirect(action: 'report', params: params + [to: new Date().format('MM/dd/yyyy')])

        Date from = new Date(params.from).clearTime()
        Date to = (new Date(params.to) + 1).clearTime()
        use(TimeCategory) {
            to = to - 1.second
        }

        def teams = formatTeams()
        def crossOverLogs = crossOverService.getWorkingHours(from, to, teams)

        def worklogs = reportService.getWorklogs(from, to, formatProjects(), formatIssueTypes(), formatComponents(), formatClients(), formatUsersList(), formatTeamMembers(crossOverLogs.keySet() as Set<String>), teams?.size > 0, formatWorklogTypes(), formatStatus())

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

    private List<JiraUser> formatUsersList() {
        JiraUser.findAllByDisplayNameInList((params.user?.split(',')?.collect {
            it.split('\\(')?.first()?.replace(')', '')?.trim()
        }?.findAll { it } ?: []) + ['-'])
    }

    private List<JiraUser> formatTeamMembers(Set<String> users) {
        JiraUser.findAllByDisplayNameInList((users?.toList() ?: []) + ['-'])
    }

    private List<String> formatWorklogTypes() {
        params.worklogTypes?.split(',')?.collect { it.split('\\(')?.last()?.replace(')', '')?.trim() }?.findAll {
            it
        } ?: []
    }

    private List<Project> formatProjects() {
        Project.findAllByKeyInList((params.project?.split(',')?.collect { it.toString()?.trim() }?.findAll {
            it
        } ?: []) + ['-'])
    }

    private List<IssueType> formatIssueTypes() {
        IssueType.findAllByNameInList((params.issueType?.split(',')?.collect { it.toString()?.trim() }?.findAll {
            it
        } ?: []) + ['-'])
    }

    private List<Component> formatComponents() {
        Component.findAllByNameInList((params.component?.split(',')?.collect { it.toString()?.trim() }?.findAll {
            it
        } ?: []) + ['-'])
    }

    private List<Client> formatClients() {
        Client.findAllByNameInList((params.client?.split(',')?.collect { it.toString()?.trim() }?.findAll {
            it
        } ?: []) + ['-'])
    }

    private List<Status> formatStatus() {
        def list = []
        params.status?.split(',')?.collect { it.toString()?.trim() }?.findAll { it }?.each { st ->
            list.addAll(Configuration.statusList.find { it.name == st }.details)
        }
        Status.findAllByNameInList(list + ['-'])
    }

    private List<String> formatTeams() {
        params.team?.split(',')?.collect { it.toString()?.trim() }?.findAll { it } ?: []
    }
}
