package jirareporter

import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured

@Secured([Roles.ADMIN])
class TestController {

    def recurringTaskService

    def index() {

        def setting = RecurringTaskSetting.findByEnabled(true)
        recurringTaskService.execute(setting)
        render 'DONE'
    }
}
