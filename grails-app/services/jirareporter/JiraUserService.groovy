package jirareporter

import grails.gorm.transactions.Transactional
import org.springframework.security.core.authority.SimpleGrantedAuthority

@Transactional
class JiraUserService {

    def authenticate(String username, String password) {
        [
                principal  : User.findByIdGreaterThan(0),
                authorities: [new SimpleGrantedAuthority(Roles.JIRA_USER)]
        ]
    }
}
