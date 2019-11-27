package jirareporter

import grails.gorm.transactions.Transactional
import org.grails.web.json.JSONArray

@Transactional
class SyncService {

    def queryService
    def cacheService
    def issueService
    def worklogService

    final String defaultProjectsList = Configuration.projects.collect { it.key }.join(',')
    final String defaultIssueTypeList = Configuration.issueTypes.collect { "\"${it}\"" }.join(',')

    void getWorklogs(Date from, Date to) {

        String worklogQyery = "project in (${defaultProjectsList}) AND (labels not in (Legacy) OR labels is EMPTY) AND issuetype in (${defaultIssueTypeList})"

        def result = queryService.execute("${worklogQyery} AND worklogDate >= '${from.format('yyyy/MM/dd')}' AND worklogDate <= '${to.format('yyyy/MM/dd')}'")
        def tasks = [:]
        result.issues?.each { issue ->
            tasks.put(issue.key, [
                    url: Configuration.serverURL + issue.self.path
            ])
        }

        def jiraClient = new JiraRestClient(new URI(Configuration.serverURL), JiraRestClient.getClient(Configuration.username, Configuration.password))
        tasks.each { task ->
            def json = null
            def url = task.value.url?.toString()

            if (cacheService.has(task.value.url))
                json = cacheService.retrieve(task.value.url)
            else {
                json = jiraClient.getURL(task.value.url)
                cacheService.store(task.value.url, json)
            }
            def issue = issueService.parse(json)


            if (cacheService.has(url + '/remotelink'))
                json = cacheService.retrieve(url + '/remotelink')
            else {
                json = jiraClient.getURL(url + '/remotelink')
                cacheService.store(url + '/remotelink', json)
            }

            def list = json as JSONArray
            issueService.parseLinks(list, issue)


            if (cacheService.has(url + '/worklog'))
                json = cacheService.retrieve(url + '/worklog')
            else {
                json = jiraClient.getURL(url + '/worklog')
                cacheService.store(url + '/worklog', json)
            }

            list = json.getJSONArray('worklogs')
            worklogService.parseList(list, issue)
        }
    }
}
