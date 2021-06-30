package jirareporter

import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured

@Secured([Roles.ADMIN])
class TestController {

    def slackService
    def reminderService
    def gitHubService
    def issueDownloadService

    def index() {

//        reminderService.sendReminders()
//        gitHubService.categorizeRepositories(0, 3000)

//        def user = JiraUser.findByName('fzaker')
//        slackService.post(user.slackId, "This is a new test message")

        issueDownloadService.download('IPCST-61019')
        render 'DONE'
    }
}
