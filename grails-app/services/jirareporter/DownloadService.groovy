package jirareporter

import grails.converters.JSON
import grails.gorm.transactions.Transactional
import org.grails.web.json.JSONArray

@Transactional
class DownloadService {

    final String defaultProjectsList = Configuration.projects.collect { it.key }.join(',')
    final String defaultIssueTypeList = Configuration.issueTypes.collect { "\"${it}\"" }.join(',')

    void getWorklogs(Date from, Date to) {

        def jiraClient = new JiraRestClient(new URI(Configuration.serverURL), JiraRestClient.getClient(Configuration.username, Configuration.password))

        String worklogQyery = "project in (${defaultProjectsList}) AND (labels not in (Legacy) OR labels is EMPTY) AND issuetype in (${defaultIssueTypeList}) AND assignee in (${JiraUser.findAllByTeamIsNotNull().collect { it.name }.join(',')})"
        worklogQyery = "${worklogQyery} AND ((worklogDate >= '${from.format('yyyy/MM/dd')}' AND worklogDate <= '${to.format('yyyy/MM/dd')}') OR (updated >= '${from.format('yyyy/MM/dd')}' AND updated <= '${to.format('yyyy/MM/dd')}'))"

        def result = jiraClient.getURL("${Configuration.serverURL}/rest/api/latest/search?jql=" + URLEncoder.encode(worklogQyery, 'UTF-8') + '&maxResults=1000')
        result.issues?.myArrayList?.each { issue ->
            new IssueDownloadItem(issueKey: issue.key, source: 'Sync Service').save()
        }
    }
}
