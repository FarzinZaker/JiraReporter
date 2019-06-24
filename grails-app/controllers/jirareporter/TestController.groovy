package jirareporter


import grails.converters.JSON

class TestController {

    def reportService

    def index() {

        def result = reportService.loggedWorks()
        render result as JSON
    }
}
