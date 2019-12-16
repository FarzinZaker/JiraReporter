package jirareporter

import grails.gorm.transactions.Transactional
import org.springframework.security.core.authority.SimpleGrantedAuthority

@Transactional
class JiraUserService {

    def authenticate(String username, String password) {

        def jiraClient = new JiraRestClient(new URI(Configuration.serverURL), JiraRestClient.getClient(username, password))

        try {
            def result = jiraClient.getURLAsList("${Configuration.serverURL}/rest/api/latest/user/search?username=$username")
            def data = result.collect { it.myArrayList.myHashMap }.find {
                def name = it.name
                if (name instanceof String)
                    name = [name]
                name.any { it.toString().trim().toLowerCase() == username.trim().toLowerCase() } && it.active
            }

            if (!data)
                return null

            def user = User.findByUsername(username)
            if (!user)
                user = new User(username: username, password: 'JP!@#')

            def displayName = data.displayName
            if (displayName instanceof String)
                displayName = [displayName]
            user.displayName = displayName.find()

            user.jiraUsername = username
            user.jiraPassword = password
            user.accountLocked = false
            user.accountExpired = false
            user.passwordExpired = false
            user.enabled = true
            user.save(flush: true)

            def role = Role.findByAuthority(Roles.JIRA_USER)
            if (!UserRole.findByUserAndRole(user, role))
                new UserRole(user: user, role: role).save(flush: true)

            return [
                    principal  : user,
                    authorities: UserRole.findAllByUser(user).collect { new SimpleGrantedAuthority(it.role.authority) }
            ]
        } catch (ignored) {
            return null
        }
    }
}
