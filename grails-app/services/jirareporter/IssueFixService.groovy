package jirareporter

import grails.gorm.transactions.Transactional
import groovy.time.TimeCategory

@Transactional
class IssueFixService {

    def issueUploadService
    def issueDownloadService

    def fix(Issue issue) {

        def comments = []

        if (!issue.originalEstimate || issue.originalEstimate?.trim() == '' || issue.originalEstimateSeconds < 1) {
            issue.originalEstimate = DurationUtil.formatDuration(3600)
            comments << ' -- No Original Estimate'
        }

        if (!issue.remainingEstimate || issue.remainingEstimate?.trim() == '')
            issue.remainingEstimate = DurationUtil.formatDuration((issue.originalEstimateSeconds ?: 3600) - (issue.timeSpentSeconds ?: 0))

        IssueDownloadItem downloadItem
        if (issue.created) {
            if (!issue.startDate) {
                issue.startDate = issue.created
                comments << ' -- No Start Date'
            }

            if (!issue.dueDate)
                use(TimeCategory) {
                    issue.dueDate = issue.startDate + (issue.originalEstimateSeconds ?: 3600).toInteger().seconds
                    comments << ' -- No Due Date'
                }

            def minDueDate = issue.startDate
            use(TimeCategory) {
                minDueDate = minDueDate + 1.day
            }
            if (issue.dueDate < minDueDate)
                issue.dueDate = minDueDate

        } else {
            downloadItem = issueDownloadService.enqueue(issue.key, 'Fix Issues')
        }

        if (issue.isDirty()) {
            def comment = comments.size() ? "Fixed the following issues: \\\\${comments.join('\\\\')}" : null
            issueUploadService.enqueue(issue, 'Fix Issues', new Date(), false, comment)
        }

        issue.lastFix = new Date()
        issue.save(flush: true)

        if (downloadItem)
            IssueDownloadItem.withNewTransaction {
                downloadItem.save()
            }

//        try {
//            Issue.executeUpdate("update Issue set lastFix = :date where id = :id", [date: new Date(), id: issue.id])
//        } catch (Exception ex) {
//            println ex.message
//        }
//        issue = Issue.get(id)
//        issue.lastFix = new Date()
//        issue.save()
    }
}