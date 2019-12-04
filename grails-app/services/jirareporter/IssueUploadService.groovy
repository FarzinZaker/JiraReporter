package jirareporter

import grails.converters.JSON
import grails.gorm.transactions.Transactional

@Transactional
class IssueUploadService {

    def enqueue(Issue issue) {
        def list = []

        issue.dirtyPropertyNames.each { property ->
            if (!IssueUploadItem.findByIssueAndProperty(issue, property)) {
                list << property
                def issueSyncItem = new IssueUploadItem(issue: issue, property: property)
                if (!issueSyncItem.save())
                    throw new Exception("Unable to save sync item: ${issueSyncItem.errorMessage}")
            }
        }

        if (list)
            println list as JSON
    }
}
