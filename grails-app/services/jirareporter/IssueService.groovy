package jirareporter

import grails.gorm.transactions.Transactional
import org.codehaus.jettison.json.JSONArray
import org.codehaus.jettison.json.JSONObject

@Transactional
class IssueService {

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
                key              : obj.key,
                issueType        : [
                        url    : obj.fields.issuetype.self,
                        name   : obj.fields.issuetype.name,
                        subtask: obj.fields.issuetype.subtask,
                        icon   : obj.fields.issuetype.iconUrl
                ],
                assignee         : [
                        url         : obj.fields.assignee.self,
                        name        : obj.fields.assignee.name,
                        key         : obj.fields.assignee.key,
                        emailAddress: obj.fields.assignee.emailAddress,
                        displayName : obj.fields.assignee.displayName,
                        active      : obj.fields.assignee.active,
                        timeZone    : obj.fields.assignee.timeZone,
                        avatars     : obj.fields.assignee.avatarUrls.myHashMap,
                ],
                timeTracking     : [
                        remainingEstimate       : obj.fields.timetracking.remainingEstimate,
                        timeSpent               : obj.fields.timetracking.timeSpent,
                        remainingEstimateSeconds: obj.fields.timetracking.remainingEstimateSeconds,
                        timeSpentSeconds        : obj.fields.timetracking.timeSpentSeconds,
                ],
                status           : [
                        url : obj.fields.status.self,
                        name: obj.fields.status.name,
                        icon: obj.fields.status.iconUrl
                ],
                reporter         : [
                        url         : obj.fields.reporter.self,
                        name        : obj.fields.reporter.name,
                        key         : obj.fields.reporter.key,
                        emailAddress: obj.fields.reporter.emailAddress,
                        displayName : obj.fields.reporter.displayName,
                        active      : obj.fields.reporter.active,
                        timeZone    : obj.fields.reporter.timeZone,
                        avatars     : obj.fields.reporter.avatarUrls.myHashMap,
                ],
                components       : obj.fields.components.myArrayList.collect {
                    [
                            url : it.self,
                            name: it.name
                    ]
                },
                progress         : [
                        value  : obj.fields.progress.progress,
                        total  : obj.fields.progress.total,
                        percent: obj.fields.progress.percent
                ],
                project          : [
                        url    : obj.fields.project.self,
                        name   : obj.fields.project.name,
                        key    : obj.fields.project.key,
                        avatars: obj.fields.project.avatarUrls
                ],
                clients          : obj.fields.customfield_26105?.myArrayList?.collect { it?.replace('"', '') } ?: [],
                updated          : Date.parse("yyyy-MM-dd'T'hh:mm:ss.000+0000", obj.fields.updated),
                summary          : obj.fields.summary,
                priority         : [
                        url : obj.fields.priority.self,
                        name: obj.fields.priority.name,
                        icon: obj.fields.priority.iconUrl
                ],
                aggregateProgress: [
                        value  : obj.fields.aggregateprogress.progress,
                        total  : obj.fields.aggregateprogress.total,
                        percent: obj.fields.aggregateprogress.percent
                ],

        ]
    }
}
