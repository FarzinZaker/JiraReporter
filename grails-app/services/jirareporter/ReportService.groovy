package jirareporter


import grails.gorm.transactions.Transactional

@Transactional
class ReportService {

    def queryService
    def worklogService
    def issueService
    def cacheService

    final String defaultProjectsList = Configuration.projects.collect { it.key }.join(',')
    final String defaultIssueTypeList = Configuration.issueTypes.collect { "\"${it}\"" }.join(',')

    List<Map> getWorklogs(Date from, Date to, String projects, String issueTypes, List components, List<String> users) {

        String worklogQyery = "project in (${projects ?: defaultProjectsList}) AND (labels not in (Legacy) OR labels is EMPTY) AND issuetype in (${issueTypes ?: defaultIssueTypeList})"


        def result = queryService.execute("${worklogQyery} AND worklogDate >= '${from.format('yyyy/MM/dd')}' AND worklogDate <= '${to.format('yyyy/MM/dd')}'")
        def tasks = [:]
        result.issues?.each { issue ->
            tasks.put(issue.key, [
                    url: Configuration.serverURL + issue.self.path
            ])
        }

        def jiraClient = new JiraRestClient(new URI(Configuration.serverURL), JiraRestClient.getClient(Configuration.username, Configuration.password))
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
            worklogs.addAll(preFilter(worklogService.parseList(list), from, to, users)?.collect {
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

        worklogs = postFilter(worklogs, components)

        worklogs
    }

    List<Map> preFilter(List<Map> list, Date from, Date to, List<String> users) {
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

    List<Map> postFilter(List<Map> list, List<String> components) {
        def result = list
        result = filterComponents(result, components)
        result
    }

    List<Map> filterComponents(List<Map> list, List<String> components) {
        list?.findAll {
            def result = false
            it.task.components.each { component ->
                if (components.contains(component.name))
                    result = true
            }
            result
        } ?: []
    }
}
