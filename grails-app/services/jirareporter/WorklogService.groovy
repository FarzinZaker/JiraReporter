package jirareporter


import grails.gorm.transactions.Transactional
import org.codehaus.jettison.json.JSONArray
import org.codehaus.jettison.json.JSONObject

@Transactional
class WorklogService {

    def userService

    List<Map> parseList(JSONArray list, Issue issue) {
        def worklogs = []
        for (def i = 0; i < list.length(); i++) {
            def obj = list.getJSONObject(i)
            worklogs << parse(obj, issue)
        }
        worklogs
    }

    Worklog parse(JSONObject obj, Issue issue) {
        if (obj == JSONObject.NULL)
            return null

        def url = obj.self
        def worklog = Worklog.findByUrl(url)

        if (!worklog) {
            worklog = new Worklog(
                    url: url,
                    author: userService.parse(obj.author),
                    updateAuthor: userService.parse(obj.updateAuthor),
                    comment: JSONUtil.safeRead(obj, 'comment'),
                    created: Date.parse("yyyy-MM-dd'T'hh:mm:ss.000+0000", obj.created),
                    updated: Date.parse("yyyy-MM-dd'T'hh:mm:ss.000+0000", obj.updated),
                    started: Date.parse("yyyy-MM-dd'T'hh:mm:ss.000+0000", obj.started),
                    timeSpent: obj.timeSpent,
                    timeSpentSeconds: obj.timeSpentSeconds,
                    jiraId: obj.id,
                    issueId: obj.issueId,
                    task: issue,
                    project: issue?.project
            )
        }

        worklog.author = userService.parse(obj.author)
        worklog.updateAuthor = userService.parse(obj.updateAuthor)
        worklog.comment = JSONUtil.safeRead(obj, 'comment')
        worklog.updated = Date.parse("yyyy-MM-dd'T'hh:mm:ss.000+0000", obj.updated)
        worklog.started = Date.parse("yyyy-MM-dd'T'hh:mm:ss.000+0000", obj.started)
        worklog.timeSpent = obj.timeSpent
        worklog.timeSpentSeconds = obj.timeSpentSeconds

        if (!worklog.save(flush: true))
            throw new Exception("Error saving worklog")

        worklog
    }
}
