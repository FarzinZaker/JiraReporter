package jirareporter

import com.atlassian.jira.rest.client.RestClientException
import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured
import groovy.time.TimeCategory

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

            data << getIssueRequiredFields(issue, issues, idList)

            if (!projects.any { issue.project?.id == it.id })
                projects << [id: issue.project?.id, name: issue.project?.name, key: issue.project?.key, clients: []]
            def project = projects.find { it.id == issue.project?.id }
            def client = issue.clients?.find()
            if (!project.clients.any { it.id == client?.id })
                project.clients << [id: client?.id ?: 0, name: client?.name]

            IssueLink.findAllByDeletedAndFirstIssueAndTypeAndSecondIssueInList(false, issue, 'has to be done before', issues).each { link ->
                links << [
                        id    : link.id,
                        source: link.firstIssue?.id,
                        target: link.secondIssue?.id,
                        type  : '0'
                ]
            }
        }

        data.each { issue ->
            def children = data.findAll { it.parent == issue.id }
            if (children.size()) {
                def originalEstimateSeconds = children.collect {
                    DurationUtil.getDurationSeconds(it.originalEstimate)
                }.sum()
                def remainingEstimateSeconds = children.collect {
                    DurationUtil.getDurationSeconds(it.remainingEstimate)
                }.sum()
                def timeSpentSeconds = children.collect {
                    DurationUtil.getDurationSeconds(it.timeSpent)
                }.sum()

                def estimateMinutes = Math.ceil((originalEstimateSeconds ?: 1) / 60).toInteger()
                def estimateHours = Math.ceil(estimateMinutes / 60)

                def startDate = children.collect { it.startDate }.min()
                def dueDate = children.collect { it.dueDate }.max()

                def durationDays = 1
                if (startDate && dueDate)
                    use(TimeCategory) {
                        durationDays = (dueDate - startDate).days
                        if (durationDays < 1) {
                            durationDays = 1
                            dueDate = dueDate + 1.day
                        }
                    }

                issue.originalEstimate = DurationUtil.formatDuration(originalEstimateSeconds)
                issue.remainingEstimate = DurationUtil.formatDuration(remainingEstimateSeconds)
                issue.timeSpent = DurationUtil.formatDuration(timeSpentSeconds)
                issue.estimateHours = estimateHours

                if (issue.owner)
                    issue.owner.value = Math.round(estimateHours / durationDays).toInteger()
            }
        }

        projects.sort { it.name }.each { project ->
            data << [
                    id         : 'p' + project.id,
                    text       : project.name,
                    type       : 'project',
                    parent     : null,
                    taskType   : 'project',
                    projectKey : project.key,
                    projectName: project.name
            ]

            project.clients.findAll { client ->
                data.any { it.parent == 'p' + project.id + 'c' + (client?.id ?: 0) }
            }.sort { it.name ?: 'zzz' }.each { client ->
                data << [
                        id         : 'p' + project.id + 'c' + (client?.id ?: 0),
                        text       : client?.name ?: 'Missing',
                        type       : 'project',
                        parent     : 'p' + project.id,
                        taskType   : 'client',
                        projectKey : project.key,
                        projectName: project.name,
                        client     : client?.name
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
        def firstIssue = Issue.get(params.source)
        def secondIssue = Issue.get(params.target)
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
        def firstIssue = Issue.get(params.source)
        def secondIssue = Issue.get(params.target)
        def link = new IssueLink(firstIssue: firstIssue, secondIssue: secondIssue, type: 'has to be done before', added: true, key: '-')
        if (!link.save(flush: true))
            throw new Exception("Unable to create the link")
        render 1
    }

    def updateIssue() {
        def data = JSON.parse(params.data)
        def time = Date.parse("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", data.time)
        println time
        def issueData = data.task
        def formatter = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS'Z'")
        def issue = Issue.findByKey(issueData.key)
        issue.startDate = formatter.parse(issueData.start_date).clearTime()
        issue.dueDate = formatter.parse(issueData.end_date).clearTime()
        issue.originalEstimate = issueData.originalEstimate
        issue.priority = Priority.get(issueData.priority)
        issue.assignee = JiraUser.get(issueData.owner_id)

        issueUploadService.enqueue(issue, 'User', time, true)

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

    def createIssue() {

        def creator = springSecurityService.currentUser as User
        println params
        def issue = new Issue()
        issue.project = filterService.formatProjects(params).find()
        issue.issueType = filterService.formatIssueTypes(params).find()
        issue.priority = filterService.formatPriorities(params).find()
        issue.summary = params.summary?.trim()
        issue.description = params.description?.trim()
        issue.assignee = filterService.formatUsersList(params).find()
        issue.originalEstimate = params.originalEstimate?.trim()
//        if (params.startDate?.trim())
//            issue.startDate = Date.parse('MM/dd/yyyy', params.startDate?.trim())
        if (params.dueDate?.trim())
            issue.dueDate = Date.parse('MM/dd/yyyy', params.dueDate?.trim())

        def parent = filterService.formatIssueList(params).find()?.key
        def components = filterService.formatComponents(params)
        def client = filterService.formatClients(params).find()

        try {
            def key = issueUploadService.create(issue, client, components.toSet(), [], parent, creator, true)

            render([succeed: true, key: key] as JSON)
        } catch (RestClientException ex) {
            def exceptionFilter = 'com.sun.jersey.api.client.UniformInterfaceException'
            render([succeed: false, error: ex.message.replace(exceptionFilter, 'Jira Connection Issue')] as JSON)
        } catch (ex) {
            def exceptionFilter = 'com.sun.jersey.api.client.UniformInterfaceException'
            render([succeed: false, error: ex.message.replace(exceptionFilter, 'Jira Connection Issue')] as JSON)
        }
    }

    private Map getIssueRequiredFields(Issue issue, List<Issue> issues = [], def idList = [0l]) {

        def formatter = new SimpleDateFormat('dd-MM-yyyy hh:mm:ss')
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

        def parent = issue.parent?.id ?: IssueLink.findByDeletedAndFirstIssueAndType(false, issue, 'is child of')?.secondIssue?.id ?: IssueLink.findByDeletedAndSecondIssueAndType(false, issue, 'is parent of')?.firstIssue?.id

        if (issues?.size() && !issues.any { it.id == parent })
            parent = null

        if (!parent) {
            parent = 'p' + issue.project.id + 'c' + (issue?.clients?.find()?.id ?: 0)
        }

        [
                id               : issue.id,
                key              : issue.key,
                projectKey       : issue.project?.key,
                projectName      : issue.project?.name,
                text             : issue.summary,
                description      : markdown.renderHtml(text: issue.description),
                type             : isParent ? 'project' : 'task',
                taskType         : 'task',
                isSubTask        : issue.issueType.subtask,
                issueType        : issue.issueType.name.replace(' ', '_'),
                issueTypeIcon    : issue.issueType.icon,
                owner            : issue.assignee ? [
                        resource_id: issue.assignee.id,
                        value      : Math.round(estimateHours / durationDays).toInteger()
                ] : null,
                owner_id         : issue.assignee?.id ?: 0,
                components       : issue.components.collect { it.name }.join(','),
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
    }
}
