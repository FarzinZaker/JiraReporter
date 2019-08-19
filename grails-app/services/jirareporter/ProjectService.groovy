package jirareporter

import grails.gorm.transactions.Transactional
import org.codehaus.jettison.json.JSONObject

@Transactional
class ProjectService {

    Project parse(JSONObject obj) {
        if (obj == JSONObject.NULL)
            return null

        def name = JSONUtil.safeRead(obj, "name")
        def project = Project.findByName(name)
        if (!project) {
            project = new Project(
                    url: JSONUtil.safeRead(obj, "self"),
                    name: name,
                    key: JSONUtil.safeRead(obj, "key"),
                    avatar: JSONUtil.safeRead(obj, "avatarUrls.48x48")
            )
            project = project.save(flush: true)

            if (!project)
                throw new Exception("Error Saving Project")
        }
        project
    }
}
