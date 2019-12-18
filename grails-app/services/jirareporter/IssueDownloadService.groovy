package jirareporter

import grails.gorm.transactions.Transactional

@Transactional
class IssueDownloadService {

    def cacheService
    def issueService

    final String defaultProjectsList = Configuration.projects.collect { it.key }.join(',')
    final String defaultIssueTypeList = Configuration.issueTypes.collect { "\"${it}\"" }.join(',')

    void queueIssues(Date from, Date to) {

        def jiraClient = new JiraRestClient(new URI(Configuration.serverURL), JiraRestClient.getClient(Configuration.username, Configuration.password))

        String worklogQyery = "project in (${defaultProjectsList}) AND (labels not in (Legacy) OR labels is EMPTY) AND issuetype in (${defaultIssueTypeList}) AND assignee in (${JiraUser.findAllByTeamIsNotNull().collect { it.name }.join(',')})"
        worklogQyery = "${worklogQyery} AND updated >= '${from.format('yyyy/MM/dd')}' AND updated <= '${to.format('yyyy/MM/dd')}' order by updated"


        def startAt = 0
        def maxResults = 100
        while (true) {
            def result = jiraClient.getURL("${Configuration.serverURL}/rest/api/latest/search?jql=${URLEncoder.encode(worklogQyery, 'UTF-8')}&startAt=$startAt&maxResults=$maxResults")

            if (result.total == 0 || !result.issues?.length())
                return

            def downloadItem
            result.issues?.myArrayList?.each { issue ->

                def updated = JiraIssueMapper.getFieldValue(issue, 'updated')
                def savedIssue = Issue.findByKey(issue.key)
                if (updated > savedIssue.lastSync)
                    downloadItem = new IssueDownloadItem(issueKey: issue.key, source: 'Sync Service').save()
            }
            if (downloadItem && !downloadItem.save(flush: true))
                throw new Exception('Unable to save Download Item')

            if (result.issues?.length() < maxResults)
                return

            startAt += maxResults
        }
    }

    def download(String issueKey) {

        if (!issueKey) {
            println "Issue key is NULL"
            return
        }

        def jiraClient = new JiraRestClient(new URI(Configuration.serverURL), JiraRestClient.getClient(Configuration.username, Configuration.password))

        String worklogQyery = "key = ${issueKey}"

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

            IssueDownloadItem.findAllByIssueKey(issue.key).each {
                try {
                    it.delete()
                } catch (ex) {
                    println ex
                    println it
                }
            }
        }
    }
}
