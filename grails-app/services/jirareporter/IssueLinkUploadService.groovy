package jirareporter

import grails.gorm.transactions.Transactional

@Transactional
class IssueLinkUploadService {

    def issueLinkTypeService

    def addLink(IssueLink link) {

        def data = [
                type        : [
                        name: issueLinkTypeService.getIssueLinkTypeName(link.type)
                ],
                inwardIssue : [
                        key: link.firstIssue.key
                ],
                outwardIssue: [
                        key: link.secondIssue.key
                ]
        ]


        def jiraClient = new JiraRestClient(new URI(Configuration.serverURL), JiraRestClient.getClient(Configuration.username, Configuration.password))
        jiraClient.post("${Configuration.serverURL}/rest/api/latest/issueLink", data)
        new IssueDownloadItem(issue: link.firstIssue, source: 'Add Link').save()
        new IssueDownloadItem(issue: link.secondIssue, source: 'Add Link').save()
        link.added = false
        link.save(flush: true)
    }

    def removeLink(IssueLink link) {
        def jiraClient = new JiraRestClient(new URI(Configuration.serverURL), JiraRestClient.getClient(Configuration.username, Configuration.password))
        jiraClient.delete("${Configuration.serverURL}/rest/api/latest/issueLink/${link.key}")
        new IssueDownloadItem(issue: link.firstIssue, source: 'Remove Link').save()
        new IssueDownloadItem(issue: link.secondIssue, source: 'Remove Link').save()
        link.delete(flush: true)
    }
}
