package jirareporter

import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured

import java.text.SimpleDateFormat

@Secured([Roles.ADMIN, Roles.MANAGER, Roles.JIRA_USER])
class PlannerController {

    def componentService
    def filterService
    def issueReportService
    def issueUploadService
    def springSecurityService
    def userService

    def index() {
        redirect(action: 'gantt')
    }

    def gantt() {
        if (params.findAll { it.value }.size() < 3) {
            redirect(uri: "/planner/gantt?status=${['Draft', 'To Do', 'In Progress'].join(',')}")
            return
        }
        def components = componentService.getAll(Configuration.projects.collect { it.key?.toString() })
        def clients = Client.list()
        [components: components, clients: clients, managedUsers: userService.managedUsers()]
    }

    def issues() {

        if (params.findAll { it.value && !it.key?.toString()?.toLowerCase()?.startsWith('dhxr') }.size() < 3) {
            redirect(uri: "/planner/issues?status=${['Draft', 'To Do', 'In Progress'].join(',')}")
            return
        }

        def formatter = new SimpleDateFormat('dd-MM-yyyy hh:mm:ss')

        def data = []
        def links = []

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
                teams?.size() > 0,
                filterService.formatStatus(params))

        def projects = []

        def idList = issues.collect { it.id } ?: [0]
        issues.each { issue ->

            def completed = Configuration.statusList['Verification'].contains(issue.status.name) || Configuration.statusList['Closed'].contains(issue.status.name)

            def originalEstimateSeconds = issue.originalEstimateSeconds
            if (!originalEstimateSeconds) {
                if (issue.originalEstimate && issue.originalEstimate.trim() != '') {
                    originalEstimateSeconds = DurationUtil.getDurationSeconds(issue.originalEstimate)
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

            def isParent = Issue.findByParentAndIdInList(issue, idList) ?: IssueLink.findByDeletedAndSecondIssueAndTypeAndFirstIssueInList(false, issue, 'is child of', issues) ?: IssueLink.findByDeletedAndFirstIssueAndTypeAndSecondIssueInList(false, issue, 'is parent of', issues)

            def parent = issue.parent?.key ?: IssueLink.findByDeletedAndFirstIssueAndType(false, issue, 'is child of')?.secondIssue?.key ?: IssueLink.findByDeletedAndSecondIssueAndType(false, issue, 'is parent of')?.firstIssue?.key

            if (!issues.any { it.key == parent })
                parent = null

            if (!projects.any { issue.project?.id == it.id })
                projects << [id: issue.project?.id, name: issue.project?.name, clients: []]
            def project = projects.find { it.id == issue.project?.id }
            def client = issue.clients?.find()
            if (!project.clients.any { it.id == client?.id })
                project.clients << [id: client?.id ?: 0, name: client?.name]

            if (!parent) {
                parent = 'p' + issue.project.id + 'c' + (client?.id ?: 0)
            }

            data << [
                    id               : issue.key,
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
                    owner_id         : issue.assignee?.id ?: 0,
                    estimateHours    : estimateHours,
                    durationDays     : durationDays,
                    start_date       : issue.startDate ? formatter.format(issue.startDate) : null,
                    end_date         : issue.dueDate ? formatter.format(issue.dueDate) : null,
                    updated          : issue.updated ? formatter.format(issue.updated) : null,
                    lastSync         : issue.lastSync ? formatter.format(issue.lastSync) : null,
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
                    originalEstimate : issue.originalEstimate,
                    remainingEstimate: issue.remainingEstimate,
                    timeSpent        : issue.timeSpent,
                    overdue          : !completed && issue.dueDate && issue.dueDate < new Date()
            ]

            IssueLink.findAllByDeletedAndFirstIssueAndTypeAndSecondIssueInList(false, issue, 'has to be done before', issues).each { link ->
                links << [
                        id    : link.id,
                        source: link.firstIssue?.key,
                        target: link.secondIssue?.key,
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

            project.clients.findAll { client ->
                data.any { it.parent == 'p' + project.id + 'c' + (client?.id ?: 0) }
            }.sort { it.name ?: 'zzz' }.each { client ->
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

    def deleteLink() {
//        println params
        def firstIssue = Issue.findByKey(params.source)
        def secondIssue = Issue.findByKey(params.target)
        IssueLink.findByFirstIssueAndSecondIssueAndType(firstIssue, secondIssue, 'has to be done before').each {
            it.deleted = true
            if (!it.save(flush: true))
                throw new Exception('Unable to delete the link')
        }
//        IssueLink.executeUpdate("update IssueLink set deleted = :deleted where firstIssue = :firstIssue and secondIssue = :secondIssue", [deleted: true, firstIssue: firstIssue, secondIssue: secondIssue])
        render 1
    }

    def addLink() {
//        println params
        def firstIssue = Issue.findByKey(params.source)
        def secondIssue = Issue.findByKey(params.target)
        def link = new IssueLink(firstIssue: firstIssue, secondIssue: secondIssue, type: 'has to be done before', added: true, key: '-')
        if (!link.save(flush: true))
            throw new Exception("Unable to create the link")
        render 1
    }

    def updateIssue() {

        def issueData = JSON.parse(params.issue)
        def formatter = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS'Z'")
        def issue = Issue.findByKey(issueData.key)
        issue.startDate = formatter.parse(issueData.start_date).clearTime()
        issue.dueDate = formatter.parse(issueData.end_date).clearTime()
        issue.originalEstimate = issueData.originalEstimate
        issue.priority = Priority.get(issueData.priority)
        issue.assignee = JiraUser.get(issueData.owner_id)

        issueUploadService.enqueue(issue, 'false', true)

        render 1
    }

    def addToDownloadQueue() {
        def saved = false
        while (!saved) {
            try {
                if (!new IssueDownloadItem(issueKey: params.id, source: 'MANUAL').save(flush: true))
                    throw new Exception("Unable to add new key to the download queue")
                saved = true
            } catch (ex) {
                println ex.message
                Thread.sleep(2000)
            }
        }
        render 1
    }
}
