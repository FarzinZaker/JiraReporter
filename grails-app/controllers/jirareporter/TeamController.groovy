package jirareporter

import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured

class TeamController {

    @Secured([Roles.ADMIN])
    def list() {
    }

    @Secured([Roles.ADMIN])
    def listJSON() {

        def value = [:]
        def parameters = [offset: params.skip, max: params.pageSize, sort: params["sort[0][field]"] ?: "name", order: params["sort[0][dir]"] ?: "asc"]

        def list
        list = Team.findAllByDeleted(false, parameters)
        value.total = Team.count()

        value.data = list.collect {
            [
                    id         : it.id,
                    name       : it.name,
                    xoName     : it.xoName,
                    xoKey      : it.xoKey,
                    xoManagerId: it.xoManagerId
            ]
        }

        render value as JSON
    }

    @Secured([Roles.ADMIN])
    def save() {
        def models = JSON.parse(params.models).collect { it.toSpreadMap() }
        def result = []
        models.each { model ->
            def item = new Team()
            if (model.id)
                item = Team.get(model.id)

            item.name = model.name
            item.xoName = model.xoName
            item.xoKey = model.xoKey.toInteger()
            item.xoManagerId = model.xoManagerId?.toInteger()

            result << item.save(flush: true)
        }
        render(result as JSON)
    }

    @Secured([Roles.ADMIN])
    def delete() {
        def models = JSON.parse(params.models).collect { it.toSpreadMap() }
        models.each { model ->
            def item = Team.get(model.id)
            TeamManager.findAllByTeam(item).each { it.delete() }
            item.deleted = true
            item.save(flush: true)
        }
        render([] as JSON)
    }
}
