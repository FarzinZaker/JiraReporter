package jirareporter

import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured

@Secured([Roles.ADMIN])
class TestController {

    def gitHubService

    def index() {

        def startId = 0
        100.times {
            startId = gitHubService.categorizeRepositories(startId, 1000)
        }
        render 'DONE'
    }
}
