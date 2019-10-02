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
            issue = new Issue(
                    key: key,
                    parent: Issue.findByKey(JSONUtil.safeRead(obj, 'fields.parent.myHashMap.key')),
                    issueType: issueTypeService.parse(JSONUtil.safeRead(obj, 'fields.issuetype')),
                    assignee: userService.parse(JSONUtil.safeRead(obj, 'fields.assignee')),
                    remainingEstimate: JSONUtil.safeRead(obj, "fields.timetracking.remainingEstimate"),
                    timeSpent: JSONUtil.safeRead(obj, "fields.timetracking.timeSpent"),
                    remainingEstimateSeconds: JSONUtil.safeRead(obj, "fields.timetracking.remainingEstimateSeconds")?.toLong(),
                    timeSpentSeconds: JSONUtil.safeRead(obj, "fields.timetracking.timeSpentSeconds")?.toLong(),
                    status: statusService.parse(JSONUtil.safeRead(obj, 'fields.status')),
                    reporter: userService.parse(JSONUtil.safeRead(obj, 'fields.reporter')),
                    progressValue: JSONUtil.safeRead(obj, "fields.progress.progress"),
                    progressTotal: JSONUtil.safeRead(obj, "fields.progress.total"),
                    progressPercent: JSONUtil.safeRead(obj, "fields.progress.percent"),
                    project: projectService.parse(JSONUtil.safeRead(obj, 'fields.project')),
                    updated: Date.parse("yyyy-MM-dd'T'hh:mm:ss.000+0000", JSONUtil.safeRead(obj, "fields.updated")),
                    summary: JSONUtil.safeRead(obj, "fields.summary"),
                    priority: priorityService.parse(JSONUtil.safeRead(obj, 'fields.priority')),
                    aggregateProgressValue: JSONUtil.safeRead(obj, "fields.aggregateprogress.progress"),
                    aggregateProgressTotal: JSONUtil.safeRead(obj, "fields.aggregateprogress.total"),
                    aggregateProgressPercent: JSONUtil.safeRead(obj, "fields.aggregateprogress.percent")
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
        issue.remainingEstimate = JSONUtil.safeRead(obj, "fields.timetracking.remainingEstimate")
        issue.timeSpent = JSONUtil.safeRead(obj, "fields.timetracking.timeSpent")
        issue.remainingEstimateSeconds = JSONUtil.safeRead(obj, "fields.timetracking.remainingEstimateSeconds")?.toLong()
        issue.timeSpentSeconds = JSONUtil.safeRead(obj, "fields.timetracking.timeSpentSeconds")?.toLong()
        issue.status = statusService.parse(JSONUtil.safeRead(obj, 'fields.status'))
        issue.reporter = userService.parse(JSONUtil.safeRead(obj, 'fields.reporter'))
        issue.components?.clear()
        issue.progressValue = JSONUtil.safeRead(obj, "fields.progress.progress")
        issue.progressTotal = JSONUtil.safeRead(obj, "fields.progress.total")
        issue.progressPercent = JSONUtil.safeRead(obj, "fields.progress.percent")
        issue.updated = Date.parse("yyyy-MM-dd'T'hh:mm:ss.000+0000", JSONUtil.safeRead(obj, "fields.updated"))
        issue.summary = JSONUtil.safeRead(obj, "fields.summary")
        issue.priority = priorityService.parse(JSONUtil.safeRead(obj, 'fields.priority'))
        issue.aggregateProgressValue = JSONUtil.safeRead(obj, "fields.aggregateprogress.progress")
        issue.aggregateProgressTotal = JSONUtil.safeRead(obj, "fields.aggregateprogress.total")
        issue.aggregateProgressPercent = JSONUtil.safeRead(obj, "fields.aggregateprogress.percent")
        issue.parent = Issue.findByKey(JSONUtil.safeRead(obj, 'fields.parent.myHashMap.key'))

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
        issue
    }
}
