package jirareporter

import grails.gorm.transactions.Transactional

@Transactional
class IssueLinkUploadService {

    def issueLinkTypeService
    def issueDownloadService

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
        issueDownloadService.enqueue(link.firstIssue.key, 'Add Link')
        issueDownloadService.enqueue(link.secondIssue.key, 'Add Link')
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
        issueDownloadService.enqueue(link.firstIssue.key, 'Remove Link')
        issueDownloadService.enqueue(link.secondIssue.key, 'Remove Link')
        link.delete(flush: true)
    }
}
