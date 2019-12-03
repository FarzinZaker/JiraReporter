package jirareporter

import grails.converters.JSON

import java.text.SimpleDateFormat

class PlannerController {

    def componentService
    def filterService
    def issueReportService

    def index() {
        redirect(action: 'gantt')
    }

    def gantt() {
        if (params.findAll { it.value }.size() < 3) {
            redirect(uri: "/planner/gantt?status=${['Draft', 'To Do', 'In Progress'].join(',')}&team=${Configuration.crossOverTeams.collect { it.name }.join(',')}")
            return
        }
        def components = componentService.getAll(Configuration.projects.collect { it.key?.toString() })
        def clients = Client.list()
        [components: components, clients: clients]
    }

    def issues() {
        println params
        if (params.findAll { it.value && !it.key?.toString()?.toLowerCase()?.startsWith('dhxr') }.size() < 3) {
            redirect(uri: "/planner/issues?status=${['Draft', 'To Do', 'In Progress'].join(',')}&team=${Configuration.crossOverTeams.collect { it.name }.join(',')}")
            return
        }

        def formatter = new SimpleDateFormat('dd-MM-yyyy')

        def data = []

        def teams = filterService.formatTeams(params)
        def issues = issueReportService.getIssues(
                filterService.formatProjects(params),
                filterService.formatIssueTypes(params),
                filterService.formatPriorities(params),
                filterService.formatComponents(params),
                filterService.formatClients(params),
                filterService.formatUsersList(params),
                JiraUser.findAllByTeamNameInList(teams ?: [null]),
                teams?.size > 0,
                filterService.formatStatus(params))

        def projects = []

        issues.each { issue ->

            def completed = Configuration.statusList['Verification'].contains(issue.status.name) || Configuration.statusList['Closed'].contains(issue.status.name)

            def duration = Math.ceil((issue.originalEstimateSeconds ?: 0) / 3600 / 8).toInteger()
            if (duration < 1)
                duration = 1


            def parent = issue.parent?.id ?: IssueLink.findByFirstIssueAndType(issue, 'is child of')?.secondIssue?.id ?: IssueLink.findBySecondIssueAndType(issue, 'is parent of')?.firstIssue?.id
            if (!parent) {

                if (!projects.any { issue.project?.id == it.id })
                    projects << [id: issue.project?.id, name: issue.project?.name, clients: []]
                def project = projects.find { it.id == issue.project?.id }
                def client = issue.clients?.find()
                if (!project.clients.any { it.id == client?.id })
                    project.clients << [id: client?.id ?: 0, name: client?.name]

                parent = 'p' + issue.project.id + 'c' + (client?.id ?: 0)
            }

            def dueDate = issue.dueDate ?: issue.startDate ?: issue.created ?: issue.updated
            data << [
                    id               : issue.id,
                    key              : issue.key,
                    text             : issue.summary,
                    description      : markdown.renderHtml(text: issue.description),
                    type             : 'task',
                    taskType         : 'task',
                    issueType        : issue.issueType.name.replace(' ', '_'),
                    issueTypeIcon    : issue.issueType.icon,
                    owner            : issue.assignee ? [
                            resource_id: issue.assignee.id,
                            value      : 2
                    ] : null,
                    start_date       : formatter.format((issue.startDate ?: issue.created ?: issue.updated)),
//                    end_date         : formatter.format(dueDate),
                    dueDate          : issue.dueDate ? formatter.format(dueDate) : null,
                    duration         : duration,
                    progress         : (issue.timeSpentSeconds ?: 0) / ((issue.timeSpentSeconds ?: 0) + (issue.remainingEstimateSeconds ?: 1)),
                    parent           : parent,
                    open             : true,
                    priority         : issue.priority.id,
                    priorityName     : issue.priority.name,
                    priorityIcon     : issue.priority.icon,
                    client           : issue.clients.collect { it.name }.join(', '),
                    status           : [name: issue.status.name, icon: issue.status.icon],
                    originalEstimate : [formatted: issue.originalEstimate ?: '-', value: issue.originalEstimateSeconds ?: 0],
                    remainingEstimate: [formatted: issue.remainingEstimate ?: '-', value: issue.remainingEstimateSeconds ?: 0],
                    timeSpent        : [formatted: issue.timeSpent ?: '-', value: issue.timeSpentSeconds ?: 0],
                    overdue          : !completed && issue.dueDate && issue.dueDate < new Date()
            ]
        }

        projects.sort { it.name }.each { project ->
            data << [
                    id      : 'p' + project.id,
                    text    : project.name,
                    type    : 'project',
                    parent  : null,
                    taskType: 'project'
            ]

            project.clients.sort { it.name ?: 'zzz' }.each { client ->
                data << [
                        id      : 'p' + project.id + 'c' + (client?.id ?: 0),
                        text    : client?.name ?: 'Missing',
                        type    : 'project',
                        parent  : 'p' + project.id,
                        taskType: 'client'
                ]
            }
        }

        def links = []
        render([
                data : data,
                links: links
        ] as JSON)
    }
}
