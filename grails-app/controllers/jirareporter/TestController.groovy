package jirareporter

import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured

@Secured([Roles.ADMIN])
class TestController {

    def jiraUserService

    def index() {

        render (jiraUserService.authenticate('fzaker', 'Retan!!22') as JSON)
//
//        render 'DONE'
    }
}
