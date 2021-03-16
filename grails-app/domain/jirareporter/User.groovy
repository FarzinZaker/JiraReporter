package jirareporter

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import grails.compiler.GrailsCompileStatic

@GrailsCompileStatic
@EqualsAndHashCode(includes = 'username')
@ToString(includes = 'username', includeNames = true, includePackage = false)
class User implements Serializable {

    private static final long serialVersionUID = 1

    String username
    String password
    String jiraUsername
    String jiraPassword
    String displayName
    boolean enabled = true
    boolean accountExpired
    boolean accountLocked
    boolean passwordExpired

    Set<Role> getAuthorities() {
        (UserRole.findAllByUser(this) as List<UserRole>)*.role as Set<Role>
    }

    static constraints = {
        displayName nullable: true
        password nullable: false, blank: false, password: true
        username nullable: false, blank: false, unique: true
        jiraUsername nullable: true
        jiraPassword nullable: true
    }

    static mapping = {
        password column: '`password`'
    }

    @Override
    String toString() {
        displayName
    }

    transient String getSlackId(){
        JiraUser.withNewSession {
            JiraUser.findByName(username)?.slackId
        }
    }

    transient void setSlackId(String id){
        JiraUser.withNewTransaction {
            def jiraUser = JiraUser.findByName(username)
            if (jiraUser) {
                jiraUser.slackId = id
                jiraUser.save()
            }
        }
    }
}
