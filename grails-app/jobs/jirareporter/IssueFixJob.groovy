package jirareporter

class IssueFixJob {
    static triggers = {
        simple repeatInterval: 1000l // execute job once in 5 seconds
    }

    def issueFixService

    def execute() {
        def issue = Issue.createCriteria().list {
            isNull('lastFix')
            maxResults(1)
        }?.find() as Issue

        if (issue)
            issueFixService.fix(issue)

        issue = Issue.createCriteria().list {
            order('lastFix', 'asc')
            maxResults(1)
        }?.find() as Issue

        if (issue)
            issueFixService.fix(issue)
    }
}
