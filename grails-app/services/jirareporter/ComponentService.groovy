package jirareporter

import grails.gorm.transactions.Transactional
import org.codehaus.jettison.json.JSONArray
import org.codehaus.jettison.json.JSONObject

@Transactional
class ComponentService {

    def cacheService

    List<Component> getAll(List<String> projects) {

        def jiraClient = new JiraRestClient(new URI(Configuration.serverURL), JiraRestClient.getClient(Configuration.username, Configuration.password))
        def result = []
        projects.each { project ->
            def url = "${Configuration.serverURL}/rest/api/latest/project/${project}/components"
            def json = null
            if (cacheService.has(url))
                json = cacheService.retrieve(url)
            else {
                json = jiraClient.getURLAsList(url)
                cacheService.store(url, json)
            }
            result.addAll(parseList(json as JSONArray))
        }
        result
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
}
