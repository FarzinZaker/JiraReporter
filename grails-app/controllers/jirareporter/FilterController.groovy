package jirareporter

import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured

@Secured([Roles.ADMIN, Roles.MANAGER, Roles.JIRA_USER])
class FilterController {

    def springSecurityService
    def filterService

    def createForm() {
        def id = params.id
        def parameters = params
        parameters.remove('controller')
        parameters.remove('action')
        parameters.remove('format')
        parameters.remove('_')
        parameters.remove('id')
        parameters = parameters.findAll { it.value != null && it.value?.toString()?.trim() != '' }

        render(view: 'saveForm', model: [filter: Filter.get(id), parameters: parameters, id: id])
    }

    def renameForm() {
        def filter = Filter.get(params.id)
        render(view: 'saveForm', model: [filter: filter, parameters: JSON.parse(filter.data), id: filter.id])
    }

    def save() {
        def filter = Filter.get(params.id)
        if (!filter)
            filter = new Filter()
        filter.name = params.name
        filter.data = params.data
        filter.owner = springSecurityService.currentUser as User
        if (!filter.save(flush: true))
            render 0
        else
            render filter.id
    }

    def update() {
        def filter = Filter.get(params.id)
        def parameters = params
        parameters.remove('controller')
        parameters.remove('action')
        parameters.remove('format')
        parameters.remove('_')
        parameters.remove('id')
        parameters = parameters.findAll { it.value != null && it.value?.toString()?.trim() != '' }
        filter.data = (parameters as JSON)
        if (!filter.save(flush: true))
            render 0
        else
            render filter.id
    }

    def copyForm() {
        def filter = Filter.get(params.id)
        render(view: 'saveForm', model: [filter: filter, parameters: JSON.parse(filter.data), id: filter.id])
    }

    def delete() {
        def filter = Filter.get(params.id)
        FilterUser.findAllByFilter(filter).each { it.delete(flush: true) }
        filter.delete(flush: true)
        render 1
    }

    def load() {
        def filter = Filter.get(params.id)
        redirect(controller: params.c, action: params.a, id: params.id, params: JSON.parse(filter.data))
    }

    def shareForm() {
        def filter = Filter.get(params.id)
        [users: FilterUser.findAllByFilter(filter).user]
    }

    def share() {
        def filter = Filter.get(params.id)
        def users = User.findAllByIdInList(params.users?.split(',')?.collect { it.toLong() } ?: [null])

        FilterUser.findAllByFilterAndUserNotInList(filter, users).each { it.delete(flush: true) }
        users.each { user ->
            if (!FilterUser.findByFilterAndUser(filter, user))
                new FilterUser(filter: filter, user: user).save(flush: true)
        }

        render 1
    }
}
