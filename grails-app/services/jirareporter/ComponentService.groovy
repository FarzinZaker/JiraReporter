package jirareporter

import grails.gorm.transactions.Transactional
import org.codehaus.jettison.json.JSONArray
import org.codehaus.jettison.json.JSONObject

@Transactional
class ComponentService {

    List<Component> getAll(List<String> projects) {

        Component.findAllByProjectInList(Project.findAllByKeyInList(projects))
    }

    List<Component> parseList(JSONArray list) {
        def issues = []
        for (def i = 0; i < list.length(); i++) {
            def obj = list.getJSONObject(i)
            issues << parse(obj)
        }
        issues
    }

    Component parse(JSONObject obj, Project defaultProject = null) {
        if (obj == JSONObject.NULL)
            return null

        def url = obj?.self
        def project = obj.has('project') ? Project.findByKey(obj.project) : defaultProject
        def component = Component.findByUrl(url)
        if (!component) {
            component = new Component(
                    url: url,
                    name: obj.name,
                    project: project ?: defaultProject)
            component = component.save(flush: true)
            if (!component)
                throw new Exception("Error Saving Component")
        }
        component

    }

    List<Map> updateData(Set<Component> components) {
        components.collect {
            [id: it.url.split('/').last()]
        }
    }
}
