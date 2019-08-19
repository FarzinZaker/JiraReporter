package jirareporter

import grails.gorm.transactions.Transactional
import org.codehaus.jettison.json.JSONObject

@Transactional
class StatusService {

    Status parse(JSONObject obj) {
        if (obj == JSONObject.NULL)
            return null

        def name = JSONUtil.safeRead(obj, "name")
        def status = Status.findByName(name)
        if (!status) {
            status = new Status(
                    url: JSONUtil.safeRead(obj, "self"),
                    name: name,
                    icon: JSONUtil.safeRead(obj, "iconUrl")
            )
            status = status.save(flush: true)

            if (!status)
                throw new Exception("Error Saving Status")
        }
        status
    }
}
