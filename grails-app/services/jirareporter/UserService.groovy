package jirareporter

import com.atlassian.jira.rest.client.NullProgressMonitor
import com.atlassian.jira.rest.client.internal.jersey.JerseyJiraRestClientFactory
import grails.gorm.transactions.Transactional
import org.codehaus.jettison.json.JSONArray
import org.codehaus.jettison.json.JSONObject

@Transactional
class UserService {

    def configurationService
    def cacheService

    List<Map> search(String username) {

        def jiraClient = new JiraRestClient(new URI(configurationService.serverURL), JiraRestClient.getClient(configurationService.username, configurationService.password))
        def url = "${configurationService.serverURL}/rest/api/latest/user/search?startAt=0&maxResults=10&username=${username}"
        def json = null
        if (cacheService.has(url))
            json = cacheService.retrieve(url)
        else {
            json = jiraClient.getURLAsList(url)
            cacheService.store(url, json)
        }
        parseList(json as JSONArray)
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
                url         : obj.self,
                name        : obj.name,
                key         : obj.key,
                emailAddress: obj.emailAddress,
                displayName : obj.displayName,
                active      : obj.active,
                timeZone    : obj.timeZone,
                avatars     : obj.avatarUrls.myHashMap,
        ]
    }
}
