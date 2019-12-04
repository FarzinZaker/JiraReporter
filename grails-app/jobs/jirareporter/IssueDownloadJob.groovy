package jirareporter

class IssueDownloadJob {
    static triggers = {
      simple repeatInterval: 5000l // execute job once in 5 seconds
    }

    def issueDownloadService

    def execute() {
        // execute job
    }
}
