package jirareporter


import grails.gorm.transactions.Transactional
import org.codehaus.jettison.json.JSONArray
import org.codehaus.jettison.json.JSONObject

@Transactional
class UserService {

    def cacheService

    List<JiraUser> search(String username) {

        def jiraClient = new JiraRestClient(new URI(Configuration.serverURL), JiraRestClient.getClient(Configuration.username, Configuration.password))
        def url = "${Configuration.serverURL}/rest/api/latest/user/search?startAt=0&maxResults=10&username=${username}"
        def json = null
        if (cacheService.has(url))
            json = cacheService.retrieve(url)
        else {
            json = jiraClient.getURLAsList(url)
            cacheService.store(url, json)
        }
        parseList(json as JSONArray)
    }

    List<JiraUser> parseList(JSONArray list) {
        def issues = []
        for (def i = 0; i < list.length(); i++) {
            def obj = list.getJSONObject(i)
            issues << parse(obj)
        }
        issues
    }

    JiraUser parse(def obj) {
        if (obj == JSONObject.NULL)
            return null

        def name = obj.name
        def user = JiraUser.findByName(name)
        if (!user) {
            user = new JiraUser(
                    url: obj.self,
                    name: name,
                    key: obj.key,
                    emailAddress: obj.emailAddress,
                    displayName: obj.displayName,
                    active: obj.active,
                    timeZone: obj.timeZone,
                    avatar: obj.avatarUrls.myHashMap['48x48'],
            )
            user = user.save(flush: true)

            if (!user)
                throw new Exception("Error saving user")
        }
        user
    }
}
