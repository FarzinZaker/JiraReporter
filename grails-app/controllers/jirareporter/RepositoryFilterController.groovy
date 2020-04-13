package jirareporter

import grails.plugin.springsecurity.annotation.Secured
import grails.validation.ValidationException
import static org.springframework.http.HttpStatus.*

@Secured([Roles.ADMIN])
class RepositoryFilterController {

    RepositoryFilterService repositoryFilterService

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond repositoryFilterService.list(params), model:[repositoryFilterCount: repositoryFilterService.count()]
    }

    def show(Long id) {
        respond repositoryFilterService.get(id)
    }

    def create() {
        respond new RepositoryFilter(params)
    }

    def save(RepositoryFilter repositoryFilter) {
        if (repositoryFilter == null) {
            notFound()
            return
        }

        try {
            repositoryFilterService.save(repositoryFilter)
        } catch (ValidationException e) {
            respond repositoryFilter.errors, view:'create'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'repositoryFilter.label', default: 'RepositoryFilter'), repositoryFilter.id])
                redirect repositoryFilter
            }
            '*' { respond repositoryFilter, [status: CREATED] }
        }
    }

    def edit(Long id) {
        respond repositoryFilterService.get(id)
    }

    def update(RepositoryFilter repositoryFilter) {
        if (repositoryFilter == null) {
            notFound()
            return
        }

        try {
            repositoryFilterService.save(repositoryFilter)
        } catch (ValidationException e) {
            respond repositoryFilter.errors, view:'edit'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'repositoryFilter.label', default: 'RepositoryFilter'), repositoryFilter.id])
                redirect repositoryFilter
            }
            '*'{ respond repositoryFilter, [status: OK] }
        }
    }

    def delete(Long id) {
        if (id == null) {
            notFound()
            return
        }

        repositoryFilterService.delete(id)

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'repositoryFilter.label', default: 'RepositoryFilter'), id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'repositoryFilter.label', default: 'RepositoryFilter'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
