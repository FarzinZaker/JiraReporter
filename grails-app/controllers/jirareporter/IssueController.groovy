package jirareporter

import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured

@Secured([Roles.ADMIN, Roles.MANAGER, Roles.JIRA_USER])
class IssueController {

    def issueService

    def search() {
        render((params.id ? issueService.search(params.id)?.collect {
            [
                    value: "${it.key}: ${it.summary}"
            ]
        } : []) as JSON)
    }
}
