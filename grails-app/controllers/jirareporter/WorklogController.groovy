package jirareporter

class WorklogController {

    def reportService
    def refinementService

    def report() {

        def reportDays = params.id?.toString()?.toInteger() ?: 14

        def worklogs = reportService.getWorklogs(reportDays)
        def summary = refinementService.getDeveloperSummary(worklogs)
        def clientSummary = refinementService.getClientSummary(worklogs)
        def componentSummary = refinementService.getComponentSummary(worklogs)
        def projectSummary = refinementService.getProjectSummary(worklogs)


        [worklogs: worklogs, summary: summary, clientSummary: clientSummary, componentSummary: componentSummary, projectSummary: projectSummary]
    }
}
