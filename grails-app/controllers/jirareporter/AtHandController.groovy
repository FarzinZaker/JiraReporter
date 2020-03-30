package jirareporter

import grails.plugin.springsecurity.annotation.Secured

@Secured([Roles.ADMIN])
class AtHandController {

    def atHandService

    def download() {
        atHandService.downloadIssues()

        render 'done'
    }
}
