package jirareporter

import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured

@Secured([Roles.ADMIN])
class TestController {

    def issueUploadService
    def issueLinkUploadService

    def index() {

//        def issue = Issue.read(2922)
//        issueUploadService.update(issue)
//        render(issue.key)

        //deleted unnecessary items
//        IssueLink.executeUpdate("delete IssueLink where added = :added and deleted = :deleted", [added: true, deleted: true])

        //delete removed links
        IssueLink.findAllByAddedAndDeletedAndKeyNotEqual(false, true, '-').each { link ->
            issueLinkUploadService.removeLink(link)
        }

        //create new Links
        IssueLink.findAllByAddedAndDeleted(true, false).each { link ->
            issueLinkUploadService.addLink(link)
        }

        //update added link
        IssueLink.findAllByAddedAndDeletedAndKey(false, true, '-').each { link ->
            new IssueDownloadItem(issue: link.firstIssue).save()
        }


        render 'DONE'
    }
}
