package jirareporter

import com.atlassian.jira.rest.client.JiraRestClient
import com.atlassian.jira.rest.client.NullProgressMonitor
import com.atlassian.jira.rest.client.domain.Issue
import com.atlassian.jira.rest.client.domain.SearchResult
import com.atlassian.jira.rest.client.internal.jersey.JerseyJiraRestClientFactory
import grails.gorm.transactions.Transactional

@Transactional
class QueryService {

    def configurationService

    SearchResult execute(String jql) {
        final JerseyJiraRestClientFactory factory = new JerseyJiraRestClientFactory()
        final JiraRestClient restClient = factory.createWithBasicHttpAuthentication(new URI(configurationService.serverURL), configurationService.username, configurationService.password)
        final NullProgressMonitor pm = new NullProgressMonitor()
        restClient.getSearchClient().searchJql(jql, 1000, 0, pm)
    }

    Issue getIssue(String key) {
        final JerseyJiraRestClientFactory factory = new JerseyJiraRestClientFactory()
        final JiraRestClient restClient = factory.createWithBasicHttpAuthentication(new URI(configurationService.serverURL), configurationService.username, configurationService.password)
        final NullProgressMonitor pm = new NullProgressMonitor()
        restClient.getIssueClient().getIssue(key, pm)
    }
}
