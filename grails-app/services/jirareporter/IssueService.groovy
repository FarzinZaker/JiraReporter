package jirareporter

import grails.gorm.transactions.Transactional
import org.codehaus.jettison.json.JSONArray
import org.codehaus.jettison.json.JSONObject

@Transactional
class IssueService {

    def userService

    List<Map> parseList(JSONArray list) {
        def issues = []
        for (def i = 0; i < list.length(); i++) {
            def obj = list.getJSONObject(i)
            issues << parse(obj)
        }
        issues
    }

    Map parse(JSONObject obj) {
        [
                key              : safeRead(obj, "key"),
                issueType        : [
                        url    : safeRead(obj, "fields.issuetype.self"),
                        name   : safeRead(obj, "fields.issuetype.name"),
                        subtask: safeRead(obj, "fields.issuetype.subtask"),
                        icon   : safeRead(obj, "fields.issuetype.iconUrl")
                ],
                assignee         : userService.parse(obj.fields.assignee),
                timeTracking     : [
                        remainingEstimate       : safeRead(obj, "fields.timetracking.remainingEstimate"),
                        timeSpent               : safeRead(obj, "fields.timetracking.timeSpent"),
                        remainingEstimateSeconds: safeRead(obj, "fields.timetracking.remainingEstimateSeconds"),
                        timeSpentSeconds        : safeRead(obj, "fields.timetracking.timeSpentSeconds")
                ],
                status           : [
                        url : safeRead(obj, "fields.status.self"),
                        name: safeRead(obj, "fields.status.name"),
                        icon: safeRead(obj, "fields.status.iconUrl")
                ],
                reporter         : userService.parse(obj.fields.reporter),
                components       : safeRead(obj, "fields.components.myArrayList")?.collect {
                    [
                            url : it.self,
                            name: it.name
                    ]
                },
                progress         : [
                        value  : safeRead(obj, "fields.progress.progress"),
                        total  : safeRead(obj, "fields.progress.total"),
                        percent: safeRead(obj, "fields.progress.percent"),
                ],
                project          : [
                        url    : safeRead(obj, "fields.project.self"),
                        name   : safeRead(obj, "fields.project.name"),
                        key    : safeRead(obj, "fields.project.key"),
                        avatars: safeRead(obj, "fields.project.avatarUrls")
                ],
                clients          : safeRead(obj, "fields.customfield_26105.myArrayList")?.collect {
                    it?.replace('"', '')
                } ?: [],
                updated          : Date.parse("yyyy-MM-dd'T'hh:mm:ss.000+0000", safeRead(obj, "fields.updated")),
                summary          : safeRead(obj, "fields.summary"),
                priority         : [
                        url : safeRead(obj, "fields.priority.self"),
                        name: safeRead(obj, "fields.priority.name"),
                        icon: safeRead(obj, "fields.priority.iconUrl")
                ],
                aggregateProgress: [
                        value  : safeRead(obj, "fields.aggregateprogress.progress"),
                        total  : safeRead(obj, "fields.aggregateprogress.total"),
                        percent: safeRead(obj, "fields.aggregateprogress.percent")
                ]
        ]
    }

    private static def safeRead(obj, property) {
        try {
            def value = obj
            def parts = property.split('\\.')
            parts.each { part ->
                value = value."${part}"
            }
            value
        } catch (Exception ex) {
//            println ex.message
            return '-'
        }
    }
}
