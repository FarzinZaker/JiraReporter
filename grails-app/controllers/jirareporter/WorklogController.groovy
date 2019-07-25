package jirareporter

class WorklogController {

    def reportService
    def refinementService
    def componentService
    def integrityService

    def report() {

        println params

        def components = componentService.getAll(Configuration.projects.collect { it.key?.toString() })

        if (!params.from)
            return redirect(action: 'report', params: params + [from: (new Date() - 1).format('MM/dd/yyyy')])
        if (!params.to)
            return redirect(action: 'report', params: params + [to: new Date().format('MM/dd/yyyy')])

        def worklogs = reportService.getWorklogs(new Date(params.from), new Date(params.to), params.project, formatIssueTypes(), formatComponents(), formatClients(), formatUsersList(), formatWorklogTypes())

        def clientDetails = refinementService.getClientDetails(worklogs)
        def componentDetails = refinementService.getComponentDetails(worklogs)
        def issueTypeDetails = refinementService.getIssueTypeDetails(worklogs)
        def projectDetails = refinementService.getProjectDetails(worklogs)

        def userSummary = refinementService.getDeveloperSummary(worklogs)
        def clientSummary = refinementService.getClientSummary(worklogs)
        def componentSummary = refinementService.getComponentSummary(worklogs)
        def issueTypeSummary = refinementService.getIssueTypeSummary(worklogs)
        def projectSummary = refinementService.getProjectSummary(worklogs)

        def integritySummary = integrityService.getDeveloperIntegritySummary(worklogs, new Date(params.from), new Date(params.to))

        [
                components      : components,
                worklogs        : worklogs,

                userSummary     : userSummary,
                clientSummary   : clientSummary,
                componentSummary: componentSummary,
                issueTypeSummary: issueTypeSummary,
                projectSummary  : projectSummary,

                clientDetails   : clientDetails,
                componentDetails: componentDetails,
                issueTypeDetails: issueTypeDetails,
                projectDetails  : projectDetails,

                integritySummary: integritySummary
        ]
    }

    private List<String> formatUsersList() {
        params.user?.split(',')?.collect { it.split('\\(')?.last()?.replace(')', '')?.trim() }
    }

    private List<String> formatWorklogTypes() {
        params.worklogTypes?.split(',')?.collect { it.split('\\(')?.last()?.replace(')', '')?.trim() }
    }

    private String formatIssueTypes() {
        params.issueType?.split(',')?.collect { "\"${it}\"" }?.join(',')
    }

    private List<String> formatComponents() {
        params.component?.split(',')
    }

    private List<String> formatClients() {
        params.client?.split(',')?.collect { it.toString()?.toLowerCase()?.trim() }
    }
}
