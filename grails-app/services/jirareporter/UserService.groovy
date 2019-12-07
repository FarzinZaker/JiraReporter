package jirareporter


import grails.gorm.transactions.Transactional
import org.codehaus.jettison.json.JSONArray
import org.codehaus.jettison.json.JSONObject

@Transactional
class UserService {

    List<JiraUser> search(String username) {
        def nameList = CrossOverLog.createCriteria().list {
            projections {
                distinct('name')
            }
        }
        JiraUser.createCriteria().list {
            'in'('displayName', nameList)
            ilike('name', "%$username%")
            ilike('displayName', "%$username%")
        }
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
        if (!obj || obj == JSONObject.NULL)
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
