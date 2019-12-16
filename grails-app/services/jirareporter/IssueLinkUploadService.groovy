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
        new IssueDownloadItem(issueKey: link.firstIssue.key, source: 'Add Link').save()
        new IssueDownloadItem(issueKey: link.secondIssue.key, source: 'Add Link').save()
        link.added = false
        link.save(flush: true)
    }

    def removeLink(IssueLink link) {
        def jiraClient = new JiraRestClient(new URI(Configuration.serverURL), JiraRestClient.getClient(Configuration.username, Configuration.password))
        try {
            jiraClient.delete("${Configuration.serverURL}/rest/api/latest/issueLink/${link.key}")
        } catch (ex) {
            if (!ex.message.contains("No issue link with id '${link.key}' exists"))
                throw ex
        }
        new IssueDownloadItem(issueKey: link.firstIssue.key, source: 'Remove Link').save()
        new IssueDownloadItem(issueKey: link.secondIssue.key, source: 'Remove Link').save()
        link.delete(flush: true)
    }
}
