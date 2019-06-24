package jirareporter


import grails.gorm.transactions.Transactional
import groovy.time.TimeCategory

@Transactional
class ReportService {

    def configurationService
    def queryService
    def worklogService

    final String worklogQyery = 'project in (PLTAKE, PLBECK, PLSMS, Platinum-NorthPlains) AND (labels not in (Legacy) OR labels is EMPTY) AND issuetype in (Bugfix, Defect, Development, Documentation, Pairing, "R&D", Story, Task, Test, "Bugfix Sub-Task", "Development Sub-Task", "Documentation Sub-Task", "Pairing Sub-Task", "R&D Sub-Task", Sub-task, "Test Sub-Task")'

    List<Map> getWorklogs(Integer daysBefore) {
        def result = queryService.execute("${worklogQyery} AND worklogDate > -${daysBefore}d")
        def tasks = [:]
        result.issues?.each { issue ->
            tasks.put(issue.key, [
                    url: configurationService.serverURL + issue.self.path
            ])
        }

        def worklogClient = new JiraWorklogRestClient(new URI(configurationService.serverURL), JiraWorklogRestClient.getClient(configurationService.username, configurationService.password))
        def worklogs = []
        tasks.each { task ->
            def json = worklogClient.getURL(task.value.url?.toString() + '/worklog')
            def list = json.getJSONArray('worklogs')
            worklogs.addAll(worklogService.parseList(list)?.collect {
                def map = it
                it.task = [id: task.key, URL: task.value.url]
                it
            })
        }

        def startDate = new Date()
        use(TimeCategory) {
            startDate = startDate - (daysBefore).days
        }

        worklogs?.findAll { it.updated >= startDate }
    }
}
