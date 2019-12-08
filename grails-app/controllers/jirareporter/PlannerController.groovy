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
        if (params.findAll { it.value && !it.key?.toString()?.toLowerCase()?.startsWith('dhxr') }.size() < 3) {
            redirect(uri: "/planner/issues?status=${['Draft', 'To Do', 'In Progress'].join(',')}&team=${Configuration.crossOverTeams.collect { it.name }.join(',')}")
            return
        }

        def formatter = new SimpleDateFormat('dd-MM-yyyy hh:mm:ss')

        def data = []
        def links = []

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

        def idList = issues.collect{it.id} ?: [0]
        issues.each { issue ->

            def completed = Configuration.statusList['Verification'].contains(issue.status.name) || Configuration.statusList['Closed'].contains(issue.status.name)

            def originalEstimateSeconds = issue.originalEstimateSeconds
            if (!originalEstimateSeconds) {
                if (issue.originalEstimate && issue.originalEstimate.trim() != '') {
                    originalEstimateSeconds = getDurationSeconds(issue.originalEstimate)
                } else
                    originalEstimateSeconds = 3600
            }
            def estimateMinutes = Math.ceil((originalEstimateSeconds ?: 1) / 60).toInteger()
            def estimateHours = Math.ceil(estimateMinutes / 60)
//            def estimateDays = Math.ceil(estimateHours / 8)

            def durationDays = 1
            if (issue.startDate && issue.dueDate)
                use(groovy.time.TimeCategory) {
                    durationDays = (issue.dueDate - issue.startDate).days
                    if (durationDays < 1) {
                        durationDays = 1
                        issue.dueDate = issue.dueDate + 1.day
                    }
                }

            def isParent = Issue.findByParentAndIdInList(issue, idList) ?: IssueLink.findBySecondIssueAndTypeAndFirstIssueInList(issue, 'is child of', issues) ?: IssueLink.findByFirstIssueAndTypeAndSecondIssueInList(issue, 'is parent of', issues)

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

            data << [
                    id               : issue.id,
                    key              : issue.key,
                    text             : issue.summary,
                    description      : markdown.renderHtml(text: issue.description),
                    type             : isParent ? 'project' : 'task',
                    taskType         : 'task',
                    issueType        : issue.issueType.name.replace(' ', '_'),
                    issueTypeIcon    : issue.issueType.icon,
                    owner            : issue.assignee ? [
                            resource_id: issue.assignee.id,
                            value      : Math.round(estimateHours / durationDays).toInteger()
                    ] : null,
                    start_date       : issue.startDate ? formatter.format(issue.startDate) : null,
                    end_date         : issue.dueDate ? formatter.format(issue.dueDate) : null,
//                    dueDate          : issue.dueDate ? formatter.format(dueDate) : null,
//                    duration         : durationDays,
                    progress         : (issue.timeSpentSeconds ?: 0) / ((issue.timeSpentSeconds ?: 0) + (issue.remainingEstimateSeconds ?: 1)),
                    parent           : parent,
                    open             : true,
                    priority         : issue.priority.id,
                    priorityName     : issue.priority.name,
                    priorityIcon     : issue.priority.icon,
                    client           : issue.clients.collect { it.name }.join(', '),
                    status           : [name: issue.status.name, icon: issue.status.icon],
                    originalEstimate : [formatted: issue.originalEstimate ?: '-', value: originalEstimateSeconds ?: 0],
                    remainingEstimate: [formatted: issue.remainingEstimate ?: '-', value: issue.remainingEstimateSeconds ?: 0],
                    timeSpent        : [formatted: issue.timeSpent ?: '-', value: issue.timeSpentSeconds ?: 0],
                    overdue          : !completed && issue.dueDate && issue.dueDate < new Date()
            ]

            IssueLink.findAllByFirstIssueAndTypeAndSecondIssueInList(issue, 'has to be done before', issues).each { link ->
                links << [
                        id    : link.id,
                        source: link.firstIssue?.id,
                        target: link.secondIssue?.id,
                        type  : '0'
                ]
            }
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

        render([
                data : data,
                links: links
        ] as JSON)
    }

    def syncStatus() {
        render([
                upload  : IssueUploadItem.createCriteria().list {
                    lt('retryCount', 20)
                    projections {
                        property('issue')
                    }
                }.unique().size(),
                download: IssueDownloadItem.count()
        ] as JSON)
    }

    private getDurationSeconds(String duration) {
        if (!duration)
            return 0

        def seconds = 0
        def parts = duration.split(' ').collect { it.trim() }.findAll { it && !it == '' }
        parts.each { dur ->
            if (dur.endsWith('w'))
                seconds += dur.replace('w', '').toInteger() * 60 * 60 * 8 * 5
            else if (dur.endsWith('d'))
                seconds += dur.replace('d', '').toInteger() * 60 * 60 * 8
            else if (dur.endsWith('h'))
                seconds += dur.replace('h', '').toInteger() * 60 * 60
            else
                seconds += dur.replace('m', '').toInteger() * 60 * 60
            seconds
        }
    }
}
