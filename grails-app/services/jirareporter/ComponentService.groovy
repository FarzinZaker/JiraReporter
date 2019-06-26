package jirareporter

import grails.gorm.transactions.Transactional
import org.codehaus.jettison.json.JSONArray
import org.codehaus.jettison.json.JSONObject

@Transactional
class ComponentService {

    def cacheService

    List<Map> getAll(List<String> projects) {

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
                url    : obj.self,
                id     : obj.id,
                name   : obj.name,
                project: obj.project
        ]
    }
}
