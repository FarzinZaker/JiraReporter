package jirareporter

import grails.gorm.transactions.Transactional
import groovy.time.TimeCategory

@Transactional
class IssueFixService {

    def issueUploadService

    def fix(Issue issue) {

        def comments = []

        if (!issue.originalEstimate || issue.originalEstimate?.trim() == '' || issue.originalEstimateSeconds < 1) {
            issue.originalEstimate = formatDuration(3600)
            comments << ' * No Original Estimate'
        }

        if (!issue.remainingEstimate || issue.remainingEstimate?.trim() == '')
            issue.remainingEstimate = formatDuration((issue.originalEstimateSeconds ?: 3600) - (issue.timeSpentSeconds ?: 0))

        IssueDownloadItem downloadItem
        if (issue.created) {
            if (!issue.startDate) {
                issue.startDate = issue.created
                comments << ' * No Start Date'
            }

            if (!issue.dueDate)
                use(TimeCategory) {
                    issue.dueDate = issue.startDate + (issue.originalEstimateSeconds ?: 3600).toInteger().seconds
                    comments << ' * No Due Date'
                }

            def minDueDate = issue.startDate
            use(TimeCategory) {
                minDueDate = minDueDate + 1.day
            }
            if (issue.dueDate < minDueDate)
                issue.dueDate = minDueDate

        } else {
            downloadItem = new IssueDownloadItem(issueKey: issue.key, source: 'Fix Issues')
        }

        if(issue.isDirty()) {
            def comment = comments.size() ? "Fixed the following issues: \\\\${comments.join('\\\\')}" : null
            issueUploadService.enqueue(issue, 'Fix Issues', comment)
        }

        issue.lastFix = new Date()
        issue.save()

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


    String formatDuration(Long time) {
        def secs = time
        def mins = 0
        def hours = 0
        def days = 0

        mins = ((secs - (secs % 60)) / 60).toInteger()
        secs = (secs % 60).toInteger()

        hours = ((mins - (mins % 60)) / 60).toInteger()
        mins = (mins % 60).toInteger()

        days = ((hours - (hours % 8)) / 8).toInteger()
        hours = (hours % 8).toInteger()

        def timeSpent = ''
        if (days > 0) {
            if (timeSpent != '')
                timeSpent += ' '
            timeSpent += days + 'd'
        }
        if (hours > 0) {
            if (timeSpent != '')
                timeSpent += ' '
            timeSpent += hours + 'h'
        }
        if (mins > 0) {
            if (timeSpent != '')
                timeSpent += ' '
            timeSpent += mins + 'm'
        }

        if (timeSpent == '')
            timeSpent = '0'

        return timeSpent
    }

}