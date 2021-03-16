package jirareporter

import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured

class ReminderController {

    @Secured([Roles.ADMIN])
    def list() {
    }

    @Secured([Roles.ADMIN])
    def listJSON() {

        def value = [:]
        def parameters = [offset: params.skip, max: params.pageSize, sort: params["sort[0][field]"] ?: "name", order: params["sort[0][dir]"] ?: "asc"]

        def list = Reminder.findAll(parameters)
        value.total = Reminder.count()

        value.data = list.collect {
            [
                    id      : it.id,
                    name    : it.name,
                    template: it.template,
                    query   : it.query,
                    emptyMessage   : it.emptyMessage,
            ]
        }

        render value as JSON
    }

    @Secured([Roles.ADMIN])
    def save() {
        def models = JSON.parse(params.models).collect { it.toSpreadMap() }
        def result = []
        models.each { model ->
            def item = new Reminder()
            if (model.id)
                item = Reminder.get(model.id)

            item.name = model.name
            item.template = model.template
            item.query = model.query
            item.emptyMessage = model.emptyMessage

            result << item.save(flush: true)
        }
        render(result as JSON)
    }

    @Secured([Roles.ADMIN])
    def delete() {
        def models = JSON.parse(params.models).collect { it.toSpreadMap() }
        models.each { model ->
            def item = Reminder.get(model.id)
            item.delete(flush: true)
        }
        render([] as JSON)
    }
}
