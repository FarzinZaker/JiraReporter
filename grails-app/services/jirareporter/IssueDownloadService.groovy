package jirareporter

import grails.gorm.transactions.Transactional

@Transactional
class IssueDownloadService {

    def cacheService
    def issueService
    def worklogService

    def download(String issueKey) {

        if(!issueKey){
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


            if (cacheService.has(url + '/worklog'))
                json = cacheService.retrieve(url + '/worklog')
            else {
                json = jiraClient.getURL(url + '/worklog')
                cacheService.store(url + '/worklog', json)
            }

            def list = json.getJSONArray('worklogs')
            worklogService.parseList(list, issue)

            IssueDownloadItem.findAllByIssue(issue).each {
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
