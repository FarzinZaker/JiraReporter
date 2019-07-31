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

    List<Map> getWorklogs(Date from, Date to, String projects, String issueTypes, List<String> components, List<String> clients, List<String> users, List<String> worklogTypes, List<String> statusList) {

        String worklogQyery = "project in (${projects && projects.trim() != '' ? projects : defaultProjectsList}) AND (labels not in (Legacy) OR labels is EMPTY) AND issuetype in (${issueTypes && issueTypes?.replace('"', '')?.trim() != '' ? issueTypes : defaultIssueTypeList})"


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
            worklogs.addAll(preFilter(worklogService.parseList(list), from, to, users, worklogTypes)?.collect {
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

        worklogs = postFilter(worklogs, components, clients, statusList)

        worklogs
    }

    List<Map> preFilter(List<Map> list, Date from, Date to, List<String> users, List<String> worklogTypes) {
        def result = list
        result = filterDates(result, from, to)
        result = filterWorklogTypes(result, worklogTypes?.findAll { it })
        result = filterUsers(result, users?.findAll { it })
        result
    }

    List<Map> filterDates(List<Map> list, Date from, Date to) {
        list?.findAll {
            it.started >= from && it.started <= to
        } ?: []
    }

    List<Map> filterUsers(List<Map> list, List<String> users) {
        if (!users || users?.size() == 0)
            return list
        list?.findAll {
            users.contains(it.author.name)
        } ?: []
    }

    List<Map> postFilter(List<Map> list, List<String> components, List<String> clients, List<String> statusList) {
        def result = list
        result = filterComponents(result, components?.findAll { it })
        result = filterClients(result, clients?.findAll { it })
        result = filterStatus(result, statusList?.findAll { it })
        result
    }

    List<Map> filterComponents(List<Map> list, List<String> components) {
        if (!components || components?.size() == 0)
            return list
        list?.findAll {
            def result = false
            it.task.components.each { component ->
                if (components.contains(component.name))
                    result = true
            }
            result
        } ?: []
    }

    List<Map> filterClients(List<Map> list, List<String> clients) {
        if (!clients || clients?.size() == 0)
            return list
        list?.findAll {
            def result = false
            it.task.clients.each { client ->
                if (clients.contains(client?.toString()?.toLowerCase()?.trim()))
                    result = true
            }
            result
        } ?: []
    }

    List<Map> filterStatus(List<Map> list, List<String> statusList) {
        if (!statusList || statusList?.size() == 0)
            return list
        list?.findAll { worklog ->
            statusList.any { status ->
                Configuration.statusList.find { it.name.toUpperCase() == status }?.details?.any {
                    it == worklog.task.status.name?.toUpperCase()
                }
            }
        } ?: []
    }

    List<Map> filterWorklogTypes(List<Map> list, List<String> worklogTypes) {
        if (!worklogTypes || worklogTypes?.size() == 0)
            return list
        def result = []
        if (worklogTypes.contains('billable'))
            result.addAll(list?.findAll {
                it.comment.contains('[BILLABLE]') || it.comment.contains('[billable]') || it.comment.contains('[Billable]')
            } ?: [])
        if (worklogTypes.contains('non-billable'))
            result.addAll(list?.findAll {
                !(it.comment.contains('[BILLABLE]') || it.comment.contains('[billable]') || it.comment.contains('[Billable]'))
            } ?: [])
        result
    }
}
