package jirareporter

class IssueUploadJob {
    static triggers = {
      simple repeatInterval: 5000l // execute job once in 5 seconds
    }

    def issueUploadService

    def execute() {
        // execute job
    }
}
