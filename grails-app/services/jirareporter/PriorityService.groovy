package jirareporter

import grails.gorm.transactions.Transactional
import org.codehaus.jettison.json.JSONObject

@Transactional
class PriorityService {

    Priority parse(JSONObject obj) {
        if (obj == JSONObject.NULL)
            return null

        def name = JSONUtil.safeRead(obj, "name")
        def priority = Priority.findByName(name)
        if (!priority) {
            priority = new Priority(
                    url: JSONUtil.safeRead(obj, "self"),
                    name: name,
                    icon: JSONUtil.safeRead(obj, "iconUrl")
            )
            priority = priority.save(flush: true)

            if (!priority)
                throw new Exception("Error Saving Priority")
        }
        priority
    }

    Map updateData(Issue issue) {
        [priority: [name: issue.priority.name]]
    }
}
