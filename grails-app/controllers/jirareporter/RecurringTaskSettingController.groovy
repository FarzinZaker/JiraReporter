package jirareporter

import grails.plugin.springsecurity.annotation.Secured

@Secured([Roles.JIRA_USER])
class RecurringTaskSettingController {

    def springSecurityService

    def edit() {

        def user = JiraUser.findByName(springSecurityService.principal.username) ?: JiraUser.findByDisplayName(springSecurityService.principal.username)
        Project.list().each { project ->
            if (!RecurringTaskSetting.findByUserAndProject(user, project))
                new RecurringTaskSetting(user: user, project: project).save(flush: true)
        }
        [user: user, settings: RecurringTaskSetting.findAllByUser(user)]
    }

    def save() {

//        println params

        def user = JiraUser.get(params.user)
        Project.list().each { project ->
            def setting = RecurringTaskSetting.findByUserAndProject(user, project)
            setting.enabled = params."enabled_${project.id}" ? true : false
            setting.originalEstimate = params."estmiate_${project.id}" ?: '1h'
            setting.components.clear()
            Component.findAllByNameInList((params."component_${project.id}"?.split(',')?.collect {
                it.toString()?.trim()
            }?.findAll {
                it
            } ?: []) + ['-']).each {
                setting.addToComponents(it)
            }
            setting.save(flush: true)
        }
        flash.message = "Recurring task settings saved successfully."
        redirect(action: 'edit')
    }
}
