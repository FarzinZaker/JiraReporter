package jirareporter

import grails.converters.JSON

class TestController {

    def crossOverService

    def index() {

        Date from = new Date() - 30
        Date to = new Date()
        def result = crossOverService.getWorkingHours('3439', '1721621', from, to)
        render(result as JSON)
    }
}
