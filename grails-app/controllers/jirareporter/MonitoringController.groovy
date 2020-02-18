package jirareporter

import grails.plugin.springsecurity.annotation.Secured

@Secured([Roles.ADMIN, Roles.MANAGER, Roles.JIRA_USER])
class MonitoringController {

    def jobs() {

    }
}
