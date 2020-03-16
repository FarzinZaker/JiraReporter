package jirareporter


import grails.gorm.transactions.Transactional
import org.codehaus.jettison.json.JSONArray
import org.codehaus.jettison.json.JSONObject

@Transactional
class UserService {

    def springSecurityService

    List<JiraUser> search(String username) {
        def user = User.findByUsername(springSecurityService.principal.username)
        def users = [user.username]
        if (springSecurityService.authentication.authorities.collect { it.role }.contains(Roles.MANAGER)) {
            TeamManager.findAllByManager(user).collect { it.team }.each { team ->
                JiraUser.findAllByTeam(team).each { jUser ->
                    users << jUser.name
                }
            }
        }
        JiraUser.createCriteria().list {
            'in'('name', users)
//            'in'('displayName', nameList)
            or {
                ilike('name', "%$username%")
                ilike('displayName', "%$username%")
            }
        }
    }

    List<User> systemSearch(String username) {
        User.createCriteria().list {
            or {
                ilike('username', "%$username%")
                ilike('jiraUsername', "%$username%")
                ilike('displayName', "%$username%")
            }
        }
    }

    List<JiraUser> managedUsers() {
        def user = User.findByUsername(springSecurityService.principal.username)
        def users = JiraUser.findAllByName(user.username)
        if (springSecurityService.authentication.authorities.collect { it.role }.contains(Roles.MANAGER)) {
            TeamManager.findAllByManager(user).collect { it.team }.each { team ->
                JiraUser.findAllByTeam(team).each { jUser ->
                    users << jUser
                }
            }
        }

        users
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
                    emailAddress: JSONUtil.safeRead(obj, 'emailAddress'),
                    displayName: JSONUtil.safeRead(obj, 'displayName'),
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

    Map updateData(Issue issue) {
        [assignee: [name: issue.assignee.name]]
    }
}
