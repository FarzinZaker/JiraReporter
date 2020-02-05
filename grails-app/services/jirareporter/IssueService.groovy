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

    List<Issue> search(String phrase) {
        Issue.createCriteria().list {
            or {
                ilike('key', "%$phrase%")
                ilike('summary', "%$phrase%")
            }
        }
    }

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

        issue.lastSync = new Date()
        if (!issue.save())
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

//        IssueDownloadItem.findAllByIssueKey(issue.key).each {
//            it.delete()
//        }
//        IssueDownloadItem.executeUpdate("delete IssueDownloadItem where issue = :issue", [issue: issue])

        issue
    }

    List<Issue> parseLinks(JSONArray list, Issue issue) {
        def links = []
        for (def i = 0; i < list.length(); i++) {
            def obj = list.getJSONObject(i)
            links << parseLink(obj, issue)
        }

        links = links.findAll { it }

        IssueLink.findAllByFirstIssueAndKeyNotInListAndAdded(issue, links.collect {
            it.key
        } ?: ['-'], false).each { it.delete() }

//        IssueLink.executeUpdate("delete IssueLink where firstIssue = :issue and key not in :keyList", [issue: issue, keyList: links.collect {
//            it.key
//        } ?: ['-']])

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

        def link = IssueLink.findByFirstIssueAndTypeAndSecondIssue(issue, type, targetIssue)
        if (!link) {
            link = new IssueLink(key: key, type: type, firstIssue: issue, secondIssue: targetIssue).save()
            if (!link)
                throw new Exception("Error creating Issue Link")
        } else {
            link.key = key
            link.type = type
            link.firstIssue = issue
            link.secondIssue = targetIssue
            link = link.save()
            if (!link)
                throw new Exception("Error Updating Issue Link")
        }
        link
    }

    void delete(String key) {
        return
        def issue = Issue.findByKey(key)
        if (!issue)
            return
        Worklog.executeUpdate("delete Worklog where task = :issue", [issue: issue])
        IssueUploadItem.executeUpdate("delete IssueUploadItem where issueKey = :issueKey and retryCount = 20", [issueKey: key])
        IssueUploadItem.executeUpdate("delete IssueLink where firstIssue = :issue or secondIssue = :issue", [issue: issue])
        Issue.findAllByParent(issue).each {
            delete(it.key)
        }
        issue.delete()
    }
}
