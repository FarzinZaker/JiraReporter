package jirareporter

import grails.gorm.transactions.Transactional
import grails.util.Holders
import grails.web.context.ServletContextHolder
import org.codehaus.jettison.json.JSONArray
import org.codehaus.jettison.json.JSONObject
import org.grails.web.util.GrailsApplicationAttributes

@Transactional
class IssueService {

    def componentService
    def projectService
    def clientService

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
        if (!issue)
            issue = new Issue(key: key)

        JiraIssueMapper.fieldsMap.keySet().each { field ->
            issue."${field}" = JiraIssueMapper.getFieldValue(obj, field)
        }

        if (!issue.save(flush: true))
            throw new Exception("Error saving issue")

        def parentKey = JSONUtil.safeRead(obj, "fields.parent.myHashMap.key")
        issue.parent = parentKey ? Issue.findByKey(parentKey) : null

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

        IssueDownloadItem.executeUpdate("delete IssueDownloadItem where issue = :issue", [issue: issue])

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
