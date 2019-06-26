package jirareporter

import grails.converters.JSON

class UserController {

    def userService

    def search() {
        render((params.id ? userService.search(params.id)?.collect {
            [
                    value: "${it.displayName} (${it.name})"
            ]
        } : []) as JSON)
    }
}
