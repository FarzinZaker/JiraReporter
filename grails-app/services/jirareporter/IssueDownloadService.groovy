package jirareporter

import grails.gorm.transactions.Transactional
import groovy.time.TimeCategory

@Transactional
class IssueDownloadService {

    def cacheService
    def issueService

    final String defaultProjectsList = Configuration.projects.collect { it.key }.join(',')

    void queueIssues(Date from) {

        use(TimeCategory) {
            from = from - 12.hours
        }
        def jiraClient = new JiraRestClient(new URI(Configuration.serverURL), JiraRestClient.getClient(Configuration.username, Configuration.password))

        String worklogQyery = "project in (${defaultProjectsList}) AND (labels not in (Legacy) OR labels is EMPTY)"
        worklogQyery = "${worklogQyery} AND updated >= '${from.format('yyyy/MM/dd HH:mm')}' order by updated"


        def startAt = 0
        def maxResults = 100
        while (true) {
            def result = jiraClient.getURL("${Configuration.serverURL}/rest/api/latest/search?jql=${URLEncoder.encode(worklogQyery, 'UTF-8')}&startAt=$startAt&maxResults=$maxResults&expand=renderedFields")

            if (result.total == 0 || !result.issues?.length())
                return

            IssueDownloadItem.withNewTransaction {
                def downloadItem
                result.issues?.myArrayList?.each { issue ->

                    def updated = JiraIssueMapper.getFieldValue(issue, 'updated')
                    def savedIssue = Issue.findByKey(issue.key)
                    if (!savedIssue || (updated + 1) > savedIssue.lastSync)
                        downloadItem = new IssueDownloadItem(issueKey: issue.key, source: 'Sync Service').save(flush: true)
                }
                if (downloadItem && !downloadItem.save(flush: true))
                    throw new Exception('Unable to save Download Item')
            }

            if (result.issues?.length() < maxResults)
                return

            startAt += maxResults
        }
    }

    void queueIssues(Date from, Date to, Boolean forceQueue = false) {

        use(TimeCategory) {
            from = from - 12.hours
            to = to + 12.hours
        }

        def jiraClient = new JiraRestClient(new URI(Configuration.serverURL), JiraRestClient.getClient(Configuration.username, Configuration.password))

        String worklogQyery = "project in (${defaultProjectsList}) AND (labels not in (Legacy) OR labels is EMPTY)"
        worklogQyery = "${worklogQyery} AND updated >= '${from.format('yyyy/MM/dd HH:mm')}' AND  updated <= '${to.format('yyyy/MM/dd HH:mm')}' order by updated"


        def startAt = 0
        def maxResults = 100
        while (true) {
            def result = jiraClient.getURL("${Configuration.serverURL}/rest/api/latest/search?jql=${URLEncoder.encode(worklogQyery, 'UTF-8')}&startAt=$startAt&maxResults=$maxResults&expand=renderedFields")

            if (result.total == 0 || !result.issues?.length())
                return

            IssueDownloadItem.withNewTransaction {
                def downloadItem
                result.issues?.myArrayList?.each { issue ->

                    def updated = JiraIssueMapper.getFieldValue(issue, 'updated')
                    def savedIssue = Issue.findByKey(issue.key)
                    if (forceQueue || !savedIssue || (updated + 1) > savedIssue.lastSync)
                        downloadItem = new IssueDownloadItem(issueKey: issue.key, source: 'Sync Service').save(flush: true)
                }
                if (downloadItem && !downloadItem.save(flush: true))
                    throw new Exception('Unable to save Download Item')

            }

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

        def result = null
        try {
            result = jiraClient.getURL("${Configuration.serverURL}/rest/api/latest/search?jql=" + URLEncoder.encode(worklogQyery, 'UTF-8') + "&expand=renderedFields")
        } catch (Exception ex) {
            if (ex.message.contains("An issue with key '${issueKey}' does not exist for field 'key'")) {
                issueService.delete(issueKey)
                return
            } else {
                println ex.message
                throw ex
            }
        }

        if (!result.issues?.myArrayList?.any { issue ->
            issue.key == issueKey
        }) {
            issueService.delete(issueKey)
            println "Issue is moved: ${issueKey}"
        }

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
                json = jiraClient.getURL(task.value.url + '?expand=renderedFields')
                cacheService.store(task.value.url, json)
            }
            def issue = issueService.parse(json)

            issueService.parseLinks(JSONUtil.safeRead(json, 'fields.issuelinks'), issue)
        }
    }

    def removeDeleted(List<String> issueKeys) {

        if (!issueKeys?.size()) {
            println "Issue keys is Empty"
            return
        }

        def jiraClient = new JiraRestClient(new URI(Configuration.serverURL), JiraRestClient.getClient(Configuration.username, Configuration.password))

        String worklogQyery = "key in (${issueKeys.join(',')})"

        def result = null
        try {
            result = jiraClient.getURL("${Configuration.serverURL}/rest/api/latest/search?jql=" + URLEncoder.encode(worklogQyery, 'UTF-8') + '&maxResults=1000&expand=renderedFields')
        } catch (Exception ex) {
            issueKeys.each { issueKey ->
                if (ex.message.contains("An issue with key '${issueKey}' does not exist for field 'key'")) {
                    issueService.delete(issueKey)
                    println "Issue is deleted: ${issueKey}"
                }
            }
            return
        }

        issueKeys.each { issueKey ->
            if (!result.issues?.myArrayList?.any { issue ->
                issue.key == issueKey
            }) {
                issueService.delete(issueKey)
                println "Issue is moved: ${issueKey}"
            }
        }

        result.issues?.myArrayList?.each { issue ->
            if (!issueKeys?.any { issueKey ->
                issue.key == issueKey

            }) {
                def saved = false
                while (!saved) {
                    try {
                        if (!new IssueDownloadItem(issueKey: issue.key, source: 'Issue Moved').save(flush: true))
                            throw new Exception('Unable to queue issue for download')
                        saved = true
                    } catch (Exception ignore) {
                        println "retrying to queue issue for download"
                    }
                }
            }
        }

    }
}
