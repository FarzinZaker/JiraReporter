package jirareporter

import grails.converters.JSON

class TestController {

    def issueUploadService

    def index() {

        def issue = Issue.read(2922)
        issueUploadService.update(issue)
        render(issue.key)
    }
}
