package jirareporter

import grails.converters.JSON
import grails.gorm.transactions.Transactional
import org.grails.web.json.JSONArray

@Transactional
class SyncService {

    def cacheService
    def issueService
    def worklogService

    final String defaultProjectsList = Configuration.projects.collect { it.key }.join(',')
    final String defaultIssueTypeList = Configuration.issueTypes.collect { "\"${it}\"" }.join(',')

    void getWorklogs(Date from, Date to) {

        def jiraClient = new JiraRestClient(new URI(Configuration.serverURL), JiraRestClient.getClient(Configuration.username, Configuration.password))

        String worklogQyery = "project in (${defaultProjectsList}) AND (labels not in (Legacy) OR labels is EMPTY) AND issuetype in (${defaultIssueTypeList})"
        worklogQyery = "${worklogQyery} AND ((worklogDate >= '${from.format('yyyy/MM/dd')}' AND worklogDate <= '${to.format('yyyy/MM/dd')}') OR (updated >= '${from.format('yyyy/MM/dd')}' AND updated <= '${to.format('yyyy/MM/dd')}'))"

        def result = jiraClient.getURL("${Configuration.serverURL}/rest/api/latest/search?jql=" + URLEncoder.encode(worklogQyery, 'UTF-8'))
        def tasks = [:]
        result.issues?.myArrayList?.each { issue ->
            tasks.put(issue.key, [
                    url: issue.self
            ])
        }

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

            issueService.parseLinks(JSONUtil.safeRead(json, 'fields.issuelinks'), issue)


            if (cacheService.has(url + '/worklog'))
                json = cacheService.retrieve(url + '/worklog')
            else {
                json = jiraClient.getURL(url + '/worklog')
                cacheService.store(url + '/worklog', json)
            }

            def list = json.getJSONArray('worklogs')
            worklogService.parseList(list, issue)
        }
    }
}
