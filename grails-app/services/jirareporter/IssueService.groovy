package jirareporter

import grails.gorm.transactions.Transactional
import org.codehaus.jettison.json.JSONArray
import org.codehaus.jettison.json.JSONObject

@Transactional
class IssueService {

    def userService
    def issueTypeService
    def statusService
    def componentService
    def projectService
    def clientService
    def priorityService

    List<Issue> parseList(JSONArray list) {
        def issues = []
        for (def i = 0; i < list.length(); i++) {
            def obj = list.getJSONObject(i)
            issues << parse(obj)
        }
        issues
    }

    Issue parse(JSONObject obj) {
        if (obj == JSONObject.NULL)
            return null

        def key = JSONUtil.safeRead(obj, "key")

        def issue = Issue.findByKey(key)

        if (!issue) {
            def startDateStr = JSONUtil.safeRead(obj, "fields.customfield_13310")
            def dueDateStr = JSONUtil.safeRead(obj, "fields.duedate")
            issue = new Issue(
                    key: key,
                    parent: Issue.findByKey(JSONUtil.safeRead(obj, 'fields.parent.myHashMap.key')),
                    issueType: issueTypeService.parse(JSONUtil.safeRead(obj, 'fields.issuetype')),
                    assignee: userService.parse(JSONUtil.safeRead(obj, 'fields.assignee')),
                    originalEstimate: JSONUtil.safeRead(obj, "fields.timetracking.originalEstimate"),
                    remainingEstimate: JSONUtil.safeRead(obj, "fields.timetracking.remainingEstimate"),
                    timeSpent: JSONUtil.safeRead(obj, "fields.timetracking.timeSpent"),
                    originalEstimateSeconds: JSONUtil.safeRead(obj, "fields.timetracking.originalEstimateSeconds")?.toLong(),
                    remainingEstimateSeconds: JSONUtil.safeRead(obj, "fields.timetracking.remainingEstimateSeconds")?.toLong(),
                    timeSpentSeconds: JSONUtil.safeRead(obj, "fields.timetracking.timeSpentSeconds")?.toLong(),
                    status: statusService.parse(JSONUtil.safeRead(obj, 'fields.status')),
                    reporter: userService.parse(JSONUtil.safeRead(obj, 'fields.reporter')),
                    progressValue: JSONUtil.safeRead(obj, "fields.progress.progress"),
                    progressTotal: JSONUtil.safeRead(obj, "fields.progress.total"),
                    progressPercent: JSONUtil.safeRead(obj, "fields.progress.percent"),
                    project: projectService.parse(JSONUtil.safeRead(obj, 'fields.project')),
                    updated: Date.parse("yyyy-MM-dd'T'hh:mm:ss.000+0000", JSONUtil.safeRead(obj, "fields.updated")),
                    created: Date.parse("yyyy-MM-dd'T'hh:mm:ss.000+0000", JSONUtil.safeRead(obj, "fields.created")),
                    summary: JSONUtil.safeRead(obj, "fields.summary"),
                    description: JSONUtil.safeRead(obj, "fields.description"),
                    priority: priorityService.parse(JSONUtil.safeRead(obj, 'fields.priority')),
                    aggregateProgressValue: JSONUtil.safeRead(obj, "fields.aggregateprogress.progress"),
                    aggregateProgressTotal: JSONUtil.safeRead(obj, "fields.aggregateprogress.total"),
                    aggregateProgressPercent: JSONUtil.safeRead(obj, "fields.aggregateprogress.percent"),
                    startDate: startDateStr ? Date.parse("yyyy-MM-dd", startDateStr) : null,
                    dueDate: dueDateStr ? Date.parse("yyyy-MM-dd", dueDateStr) : null
            )

            if (!issue.save(flush: true))
                throw new Exception("Error saving issue")

            issue.clients?.clear()
            JSONUtil.safeRead(obj, "fields.customfield_26105.myArrayList")?.each {
                def client = clientService.parse(it)
                issue.addToClients(client)
            }
            issue.components?.clear()
            JSONUtil.safeRead(obj, "fields.components.myArrayList")?.each {
                def component = componentService.parse(it, issue.project)
                issue.addToComponents(component)
            }

            if (!issue?.save(flush: true))
                throw new Exception("Error saving issue")

            issue
        }

        issue.issueType = issueTypeService.parse(JSONUtil.safeRead(obj, 'fields.issuetype'))
        issue.assignee = userService.parse(JSONUtil.safeRead(obj, 'fields.assignee'))
        issue.originalEstimate = JSONUtil.safeRead(obj, "fields.timetracking.originalEstimate")
        issue.remainingEstimate = JSONUtil.safeRead(obj, "fields.timetracking.remainingEstimate")
        issue.timeSpent = JSONUtil.safeRead(obj, "fields.timetracking.timeSpent")
        issue.originalEstimateSeconds = JSONUtil.safeRead(obj, "fields.timetracking.originalEstimateSeconds")?.toLong()
        issue.remainingEstimateSeconds = JSONUtil.safeRead(obj, "fields.timetracking.remainingEstimateSeconds")?.toLong()
        issue.timeSpentSeconds = JSONUtil.safeRead(obj, "fields.timetracking.timeSpentSeconds")?.toLong()
        issue.status = statusService.parse(JSONUtil.safeRead(obj, 'fields.status'))
        issue.reporter = userService.parse(JSONUtil.safeRead(obj, 'fields.reporter'))
        issue.components?.clear()
        issue.progressValue = JSONUtil.safeRead(obj, "fields.progress.progress")
        issue.progressTotal = JSONUtil.safeRead(obj, "fields.progress.total")
        issue.progressPercent = JSONUtil.safeRead(obj, "fields.progress.percent")
        issue.updated = Date.parse("yyyy-MM-dd'T'hh:mm:ss.000+0000", JSONUtil.safeRead(obj, "fields.updated"))
        issue.created = Date.parse("yyyy-MM-dd'T'hh:mm:ss.000+0000", JSONUtil.safeRead(obj, "fields.created"))
        issue.summary = JSONUtil.safeRead(obj, "fields.summary")
        issue.description = JSONUtil.safeRead(obj, "fields.description")
        issue.priority = priorityService.parse(JSONUtil.safeRead(obj, 'fields.priority'))
        issue.aggregateProgressValue = JSONUtil.safeRead(obj, "fields.aggregateprogress.progress")
        issue.aggregateProgressTotal = JSONUtil.safeRead(obj, "fields.aggregateprogress.total")
        issue.aggregateProgressPercent = JSONUtil.safeRead(obj, "fields.aggregateprogress.percent")
        issue.parent = Issue.findByKey(JSONUtil.safeRead(obj, 'fields.parent.myHashMap.key'))
        def startDateStr = JSONUtil.safeRead(obj, "fields.customfield_13310")
        issue.startDate = startDateStr ? Date.parse("yyyy-MM-dd", startDateStr) : null
        def endDateStr = JSONUtil.safeRead(obj, "fields.duedate")
        issue.dueDate = endDateStr ? Date.parse("yyyy-MM-dd", endDateStr) : null

        if (!issue.save(flush: true))
            throw new Exception("Error saving issue")

        issue.components?.clear()
        JSONUtil.safeRead(obj, "fields.components.myArrayList")?.each {
            def component = componentService.parse(it, issue.project)
            issue.addToComponents(component)
        }
        issue.clients?.clear()
        JSONUtil.safeRead(obj, "fields.customfield_26105.myArrayList")?.each {
            def client = clientService.parse(it)
            issue.addToClients(client)

        }
        if (!issue.save(flush: true))
            throw new Exception("Error saving issue")

        Issue.executeUpdate("delete IssueDownloadItem where issue = :issue", [issue: issue])

        issue
    }

    List<Issue> parseLinks(JSONArray list, Issue issue) {
        def links = []
        for (def i = 0; i < list.length(); i++) {
            def obj = list.getJSONObject(i)
            links << parseLink(obj, issue)
        }

        links = links.findAll { it }

        IssueLink.executeUpdate("delete IssueLink where firstIssue = :issue and key not in :keyList", [issue: issue, keyList: links.collect {
            it.key
        } ?: ['-']])

        links
    }

    IssueLink parseLink(JSONObject obj, Issue issue) {
        if (obj == JSONObject.NULL)
            return null

        def key = JSONUtil.safeRead(obj, "id")

        def type = JSONUtil.safeRead(obj, 'type.inward')?.toString()
        def targetIssueKey = JSONUtil.safeRead(obj, 'inwardIssue.key')?.toString()

        if (!targetIssueKey) {
            type = JSONUtil.safeRead(obj, 'type.outward')?.toString()
            targetIssueKey = JSONUtil.safeRead(obj, 'outwardIssue.key')?.toString()
        }
        def targetIssue = Issue.findByKey(targetIssueKey)
        if (!targetIssue)
            return null

        def link = IssueLink.findByKey(key)
        if (!link) {
            link = new IssueLink(key: key, type: type, firstIssue: issue, secondIssue: targetIssue).save()
            if (!link)
                throw new Exception("Error creating Issue Link")
        } else {
            link.type = type
            link.firstIssue = issue
            link.secondIssue = targetIssue
            link = link.save()
            if (!link)
                throw new Exception("Error Updating Issue Link")
        }
        link
    }
}
