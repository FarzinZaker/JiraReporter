package jirareporter

import grails.converters.JSON
import grails.gorm.transactions.Transactional
import org.grails.web.json.JSONArray

@Transactional
class WorklogDownloadService {

    def cacheService
    def worklogService

    final String defaultProjectsList = Configuration.projects.collect { it.key }.join(',')

    void getWorklogs(Date from, Date to) {

        def jiraClient = new JiraRestClient(new URI(Configuration.serverURL), JiraRestClient.getClient(Configuration.username, Configuration.password))

        String worklogQyery = "project in (${defaultProjectsList}) AND (labels not in (Legacy) OR labels is EMPTY)"
        worklogQyery = "${worklogQyery} AND worklogDate >= '${from.format('yyyy/MM/dd')}' AND worklogDate <= '${to.format('yyyy/MM/dd')}' order by updated"

        def startAt = 0
        def maxResults = 100
        while (true) {
            def result = jiraClient.getURL("${Configuration.serverURL}/rest/api/latest/search?jql=${URLEncoder.encode(worklogQyery, 'UTF-8')}&startAt=$startAt&maxResults=$maxResults")

            if (result.total == 0 || !result.issues?.length())
                return

            result.issues?.myArrayList?.each { issue ->

                def savedIssue = Issue.findByKey(issue.key)
                if (savedIssue) {

                    def url = issue.self
                    def json = null
                    if (cacheService.has(url + '/worklog'))
                        json = cacheService.retrieve(url + '/worklog')
                    else {
                        try {
                            json = jiraClient.getURL(url + '/worklog')
                            cacheService.store(url + '/worklog', json)
                        } catch (Exception ex) {
                            println ex.message
                            println(url + '/worklog')
                        }
                    }

                    def list = json.getJSONArray('worklogs')
                    worklogService.parseList(list, savedIssue)
                }
            }

            if (result.issues?.length() < maxResults)
                return

            startAt += maxResults
        }
    }
}
