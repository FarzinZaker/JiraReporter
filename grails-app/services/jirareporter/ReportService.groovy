package jirareporter


import grails.gorm.transactions.Transactional
import groovy.time.TimeCategory

@Transactional
class ReportService {

    def configurationService
    def queryService
    def worklogService
    def issueService
    def cacheService

    final String worklogQyery = 'project in (PLTAKE, PLBECK, PLSMS, Platinum-NorthPlains) AND (labels not in (Legacy) OR labels is EMPTY) AND issuetype in (Bugfix, Defect, Development, Documentation, Pairing, "R&D", Story, Task, Test, "Bugfix Sub-Task", "Development Sub-Task", "Documentation Sub-Task", "Pairing Sub-Task", "R&D Sub-Task", Sub-task, "Test Sub-Task")'

    List<Map> getWorklogs(Integer daysBefore) {
        def result = queryService.execute("${worklogQyery} AND worklogDate > -${daysBefore}d")
        def tasks = [:]
        result.issues?.each { issue ->
            tasks.put(issue.key, [
                    url: configurationService.serverURL + issue.self.path
            ])
        }

        def startDate = new Date()
        use(TimeCategory) {
            startDate = startDate - (daysBefore).days
        }

        def jiraClient = new JiraRestClient(new URI(configurationService.serverURL), JiraRestClient.getClient(configurationService.username, configurationService.password))
        def worklogs = []
        tasks.each { task ->
            def json = null
            if (cacheService.has(task.value.url?.toString() + '/worklog'))
                json = cacheService.retrieve(task.value.url?.toString() + '/worklog')
            else {
                json = jiraClient.getURL(task.value.url?.toString() + '/worklog')
                cacheService.store(task.value.url?.toString() + '/worklog', json)
            }
            def list = json.getJSONArray('worklogs')
            worklogs.addAll(worklogService.parseList(list)?.findAll { it.updated >= startDate }?.collect {
                if (cacheService.has(task.value.url))
                    json = cacheService.retrieve(task.value.url)
                else {
                    json = jiraClient.getURL(task.value.url)
                    cacheService.store(task.value.url, json)
                }
                it.task = issueService.parse(json)
                it.project = it.task.project
                it
            })
        }

        worklogs
    }
}
