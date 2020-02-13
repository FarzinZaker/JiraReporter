package jirareporter

import grails.util.Holders
import org.grails.web.json.JSONObject

import java.text.SimpleDateFormat

class JiraIssueMapper {

    static Map fieldsMap = [
            startDate               : [field: 'customfield_13310', type: Date, format: "yyyy-MM-dd"],
            dueDate                 : [field: 'duedate', type: Date, format: "yyyy-MM-dd"],
//            parent                  : [field: 'parent.myHashMap.key', type: String],
            issueType               : [field: 'issuetype', type: JSONObject, parser: 'issueTypeService'],
            assignee                : [field: 'assignee', type: JSONObject, parser: 'userService'],
            originalEstimate        : [field: 'timetracking.originalEstimate', type: String],
            remainingEstimate       : [field: 'timetracking.remainingEstimate', type: String],
            timeSpent               : [field: 'timetracking.timeSpent', type: String],
            originalEstimateSeconds : [field: 'timetracking.originalEstimateSeconds', type: Long],
            remainingEstimateSeconds: [field: 'timetracking.remainingEstimateSeconds', type: Long],
            timeSpentSeconds        : [field: 'timetracking.timeSpentSeconds', type: Long],
            status                  : [field: 'status', type: JSONObject, parser: 'statusService'],
            reporter                : [field: 'reporter', type: JSONObject, parser: 'userService'],
            progressValue           : [field: 'progress.progress', type: Double],
            progressTotal           : [field: 'progress.total', type: Double],
            progressPercent         : [field: 'progress.percent', type: Double],
            project                 : [field: 'project', type: JSONObject, parser: 'projectService'],
            updated                 : [field: 'updated', type: Date, format: "yyyy-MM-dd'T'HH:mm:ss.000+0000"],
            created                 : [field: 'created', type: Date, format: "yyyy-MM-dd'T'HH:mm:ss.000+0000"],
            summary                 : [field: 'summary', type: String],
            description             : [field: 'description', type: String, rendered: true],
            priority                : [field: 'priority', type: JSONObject, parser: 'priorityService'],
            aggregateProgressValue  : [field: 'aggregateprogress.progress', type: Double],
            aggregateProgressTotal  : [field: 'aggregateprogress.total', type: Double],
            aggregateProgressPercent: [field: 'aggregateprogress.percent', type: Double]

    ]

    static def getFieldValue(org.codehaus.jettison.json.JSONObject data, String field) {
        def mapping = fieldsMap[field]
        def rawValue = JSONUtil.safeRead(data, mapping.rendered ? "renderedFields.${mapping.field}" : "fields.${mapping.field}")
        if (!rawValue)
            return null

        convertType(field, rawValue)
    }

    static def convertType(String field, rawValue) {
        def mapping = fieldsMap[field]
        if (!rawValue)
            return null

        switch (mapping.type) {
            case Date:
                return Date.parse(mapping.format as String, rawValue)
            case Double:
                def numeric = rawValue as Double
                if (numeric == null)
                    throw new Exception("Unable to parse ${mapping.field}")
                return numeric
            case Long:
                def numeric = rawValue as Long
                if (numeric == null)
                    throw new Exception("Unable to parse ${mapping.field}")
                return numeric
            case JSONObject:
                return Holders.grailsApplication.mainContext.getBean(mapping.parser).parse(rawValue)
            default:
                return rawValue.toString()
        }
    }

    static def formatType(String field, rawValue) {
        def mapping = fieldsMap[field]
        if (!rawValue)
            return rawValue

        switch (mapping.type) {
            case Date:
                return new SimpleDateFormat(mapping.format).format(rawValue)
            case Double:
                def numeric = rawValue as Double
                if (numeric == null)
                    throw new Exception("Unable to parse ${mapping.field}")
                return numeric
            case Long:
                def numeric = rawValue as Long
                if (numeric == null)
                    throw new Exception("Unable to parse ${mapping.field}")
                return numeric
            default:
                return rawValue.toString()
        }
    }
}
