package jirareporter

import grails.gorm.transactions.Transactional
import groovy.time.TimeCategory

@Transactional
class IssueFixService {

    def issueUploadService

    def fix(Issue issue) {
        if (!issue.originalEstimate || issue.originalEstimateSeconds == 0) {
            issue.originalEstimateSeconds = 3600 // 1 hour
            issue.originalEstimate = formatDuration(issue.originalEstimateSeconds)
        }

        if (!issue.remainingEstimate) {
            issue.remainingEstimateSeconds = issue.originalEstimateSeconds - (issue.timeSpentSeconds ?: 0)
            issue.remainingEstimate = formatDuration(issue.remainingEstimateSeconds)
        }

        if (issue.created) {
            if (!issue.startDate)
                issue.startDate = issue.created

            if (!issue.dueDate)
                use(TimeCategory) {
                    def daysCount = Math.ceil(issue.remainingEstimateSeconds / 3600 / 8).toInteger()
                    if (daysCount < 1)
                        daysCount = 1
                    issue.dueDate = issue.created + daysCount.days
                }
        } else {
            IssueDownloadItem downloadItem = new IssueDownloadItem(issue: issue, property: 'created')
            downloadItem.save()
        }

        issueUploadService.enqueue(issue)

        issue.lastFix = new Date()
        issue.save()
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

        return timeSpent
    }

}