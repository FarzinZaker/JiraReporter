package jirareporter

import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured

@Secured([Roles.ADMIN, Roles.MANAGER, Roles.JIRA_USER])
class ValidationController {

    def issueReportService
    def filterService
    def componentService

    def estimate() {
        if (params.findAll { it.value }.size() < 3) {
            redirect(uri: "/validation/estimate?status=${['To Do', 'In Progress'].join(',')}")
            return
        }

        def components = componentService.getAll(Configuration.projects.collect { it.key?.toString() })
        def clients = Client.list()
        [components: components, clients: clients]
    }

    def estimateJson() {
        if (params.findAll { it.value && !it.key?.toString()?.toLowerCase()?.startsWith('dhxr') }.size() < 3) {
            redirect(uri: "/validation/estimateJson?status=${['To Do', 'In Progress'].join(',')}")
            return
        }

        def teams = filterService.formatTeams(params)
        def issues = issueReportService.getIssues(
                filterService.formatIssueList(params),
                filterService.formatProjects(params),
                filterService.formatIssueTypes(params),
                filterService.formatPriorities(params),
                filterService.formatComponents(params),
                filterService.formatClients(params),
                filterService.formatUsersList(params),
                teams?.size() ? (JiraUser.findAllByTeamInList(teams) ?: [null]) : [null],
                teams?.size > 0,
                filterService.formatStatus(params),
                filterService.formatUnassigned(params)).findAll {
            !it.originalEstimate || it.originalEstimate?.trim() == ''
        }

        def data = issues.collect {
            [
                    key     : it.key,
                    summary : it.summary,
                    assignee: it.assignee?.displayName,
                    userIcon: it.assignee?.avatar,
                    taskIcon: it.issueType.icon
            ]
        }
        def total = data.size()
//        render([data: data, total: total] as JSON)
        render(data as JSON)
    }
}
