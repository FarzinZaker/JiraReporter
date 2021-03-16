package jirareporter

import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured

@Secured([Roles.ADMIN])
class TestController {

    def slackService
    def reminderService

    def index() {

        reminderService.sendReminders()

//        def user = JiraUser.findByName('fzaker')
//        slackService.post(user.slackId, "This is a new test message")
        render 'DONE'
    }
}
