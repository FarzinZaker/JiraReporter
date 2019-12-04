package jirareporter

import org.grails.web.json.JSONObject

class JiraIssueMapper {

    def mapping = [
            startDate               : [field: 'customfield_13310', type: Date],
            dueDate                 : [field: 'duedate', type: Date],
            parent                  : [field: 'parent.myHashMap.key', type: String],
            issueType               : [field: 'issuetype', type: JSONObject, parser: IssueTypeService],
            assignee                : [field: 'assignee', type: JSONObject, pareser: UserService],
            originalEstimate        : [field: 'timetracking.originalEstimate', type: String],
            remainingEstimate       : [field: 'timetracking.remainingEstimate', type: String],
            timeSpent               : [field: 'timetracking.timeSpent', type: String],
            originalEstimateSeconds : [field: 'timetracking.originalEstimateSeconds', type: Long],
            remainingEstimateSeconds: [field: 'timetracking.remainingEstimateSeconds', type: Long],
            timeSpentSeconds        : [field: 'timetracking.timeSpentSeconds', type: Long],
            status                  : [field: 'status', type: JSONObject, parser: StatusService],
            reporter                : [field: 'reporter', type: JSONObject, parser: UserService],
            progressValue           : [field: 'progress.progress', type: Double],
            progressTotal           : [field: 'progress.total', type: Double],
            progressPercent         : [field: 'progress.percent', type: Double],
            project                 : [field: 'project', type: JSONObject, parser: ProjectService],
            updated                 : [field: 'updated', type: Date],
            created                 : [field: 'created', type: Date],
            summary                 : [field: 'summary', type: String],
            description             : [field: 'description', type: String],
            priority                : [field: 'priority', type: JSONObject, parser: PriorityService],
            aggregateProgressValue  : [field: 'aggregateprogress.progress', type: Double],
            aggregateProgressTotal  : [field: 'aggregateprogress.total', type: Double],
            aggregateProgressPercent: [field: 'aggregateprogress.percent', type: Double]

    ]
}
