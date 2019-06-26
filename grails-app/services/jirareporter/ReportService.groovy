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

    final String defaultProjectsList = 'PLTAKE, PLBECK, PLSMS, PLNP'

    List<Map> getWorklogs(Date from, Date to, String projects, List<String> users) {

        String worklogQyery = "project in (${projects ?: defaultProjectsList}) AND (labels not in (Legacy) OR labels is EMPTY) AND issuetype in (Bugfix, Defect, Development, Documentation, Pairing, \"R&D\", Story, Task, Test, \"Bugfix Sub-Task\", \"Development Sub-Task\", \"Documentation Sub-Task\", \"Pairing Sub-Task\", \"R&D Sub-Task\", Sub-task, \"Test Sub-Task\")"


        def result = queryService.execute("${worklogQyery} AND worklogDate >= '${from.format('yyyy/MM/dd')}' AND worklogDate <= '${to.format('yyyy/MM/dd')}'")
        def tasks = [:]
        result.issues?.each { issue ->
            tasks.put(issue.key, [
                    url: configurationService.serverURL + issue.self.path
            ])
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
            worklogs.addAll(filter(worklogService.parseList(list), from, to, users)?.collect {
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

    List<Map> filter(List<Map> list, Date from, Date to, List<String> users) {
        def result = list
        result = filterDates(result, from, to)
        result = filterUsers(result, users)
        result
    }

    List<Map> filterDates(List<Map> list, Date from, Date to) {
        list?.findAll {
            it.updated >= from && it.updated <= to
        } ?: []
    }

    List<Map> filterUsers(List<Map> list, List<String> users) {
        list?.findAll {
            users.contains(it.author.name)
        } ?: []
    }
}
