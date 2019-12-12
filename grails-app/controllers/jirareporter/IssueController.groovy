package jirareporter

import grails.converters.JSON

class IssueController {

    def issueService

    def search() {
        render((params.id ? issueService.search(params.id)?.collect {
            [
                    value: "${it.key}: ${it.summary}"
            ]
        } : []) as JSON)
    }
}
