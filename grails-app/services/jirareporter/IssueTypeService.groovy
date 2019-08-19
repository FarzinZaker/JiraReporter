package jirareporter

import grails.gorm.transactions.Transactional
import org.codehaus.jettison.json.JSONObject

@Transactional
class IssueTypeService {

    IssueType parse(JSONObject obj) {
        if (obj == JSONObject.NULL)
            return null

        def name = JSONUtil.safeRead(obj, "name")
        def issueType = IssueType.findByName(name)
        if(!issueType) {
            issueType = new IssueType(
                    url: JSONUtil.safeRead(obj, "self"),
                    name: name,
                    subtask: JSONUtil.safeRead(obj, "subtask"),
                    icon: JSONUtil.safeRead(obj, "iconUrl")
            )
            issueType = issueType.save(flush: true)

            if (!issueType)
                throw new Exception("Error Saving Issue Type")
        }
        issueType
    }
}
