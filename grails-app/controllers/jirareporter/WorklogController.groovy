package jirareporter

class WorklogController {

    def reportService
    def refinementService
    def componentService

    def report() {

        println params

        def components = componentService.getAll(Configuration.projects.collect { it.key?.toString() })

        if (!params.from)
            return redirect(action: 'report', params: params + [from: (new Date() - 1).format('MM/dd/yyyy')])
        if (!params.to)
            return redirect(action: 'report', params: params + [to: new Date().format('MM/dd/yyyy')])

        def worklogs = reportService.getWorklogs(new Date(params.from), new Date(params.to), params.project, formatIssueTypes(), formatComponents(), formatUsersList())
        def summary = refinementService.getDeveloperSummary(worklogs)
        def clientSummary = refinementService.getClientSummary(worklogs)
        def componentSummary = refinementService.getComponentSummary(worklogs)
        def projectSummary = refinementService.getProjectSummary(worklogs)


        [components: components, worklogs: worklogs, summary: summary, clientSummary: clientSummary, componentSummary: componentSummary, projectSummary: projectSummary]
    }

    private List<String> formatUsersList() {
        params.user?.split(',')?.collect { it.split('\\(')?.last()?.replace(')', '')?.trim() }
    }

    private String formatIssueTypes() {
        params.issueType?.split(',')?.collect { "\"${it}\"" }?.join(',')
    }

    private List<String> formatComponents() {
        params.component?.split(',')
    }
}
