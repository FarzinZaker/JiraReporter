package jirareporter

import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured

@Secured([Roles.ADMIN, Roles.MANAGER, Roles.JIRA_USER])
class ResourceController {

    def issueReportService
    def filterService
    def componentService

    def allocation() {
        if (params.findAll { it.value }.size() < 3) {
            redirect(uri: "/resource/allocation?status=${['To Do', 'In Progress'].join(',')}")
            return
        }

        def components = componentService.getAll(Configuration.projects.collect { it.key?.toString() })
        def clients = Client.list()
        def labels = Label.list()
        [components: components, clients: clients, labels: labels]
    }

    def allocationJson() {
        if (params.findAll { it.value && !it.key?.toString()?.toLowerCase()?.startsWith('dhxr') }.size() < 3) {
            redirect(uri: "/resource/allocationJson?status=${['To Do', 'In Progress'].join(',')}")
            return
        }

        def teams = filterService.formatTeams(params)
        def issues = issueReportService.getIssues(
                filterService.formatFromDate(params),
                filterService.formatToDate(params),
                filterService.formatIssueList(params),
                filterService.formatProjects(params),
                filterService.formatIssueTypes(params),
                filterService.formatPriorities(params),
                filterService.formatComponents(params),
                filterService.formatClients(params),
                filterService.formatLabels(params),
                filterService.formatUsersList(params),
                teams?.size() ? (JiraUser.findAllByTeamInList(teams) ?: [null]) : [null],
                teams?.size > 0,
                filterService.formatStatus(params),
                filterService.formatUnassigned(params))

        def data = issues.collect {
            [
                    project                 : it.project?.name ?: '-',
                    client                  : it.clients?.find()?.name ?: '-',
                    assignee                : it.assignee?.displayName ?: '-',
                    type                    : it.issueType?.name ?: '-',
                    userIcon                : it.assignee?.avatar,
                    originalEstimateSeconds : it.originalEstimateSeconds ?: 0,
                    remainingEstimateSeconds: it.remainingEstimateSeconds ?: 0,
                    timeSpentSeconds        : it.timeSpentSeconds ?: 0,
                    key                     : it.key,
                    summary                 : it.summary,
                    taskIcon                : it.issueType.icon
            ]
        }
        def total = data.size()
//        render([data: data, total: total] as JSON)
        render(data as JSON)
    }
}
