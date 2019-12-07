package jirareporter

import grails.gorm.transactions.Transactional
import groovy.time.TimeCategory

@Transactional
class IssueFixService {

    def issueUploadService

    def fix(Issue issue) {
//        println 'OS:' + issue.originalEstimate
//        println 'OSS:' + issue.originalEstimateSeconds
        if (!issue.originalEstimate || issue.originalEstimate?.trim() == '' || issue.originalEstimateSeconds < 1) {
            issue.originalEstimateSeconds = 3600 // 1 hour
            issue.originalEstimate = formatDuration(issue.originalEstimateSeconds)
        }

        if (!issue.remainingEstimate || issue.remainingEstimate?.trim() == '') {
            issue.remainingEstimateSeconds = issue.originalEstimateSeconds - (issue.timeSpentSeconds ?: 0)
            issue.remainingEstimate = formatDuration(issue.remainingEstimateSeconds)
        }

        IssueDownloadItem downloadItem
        if (issue.created) {
            if (!issue.startDate)
                issue.startDate = issue.created

            if (!issue.dueDate)
                use(TimeCategory) {
//                    println issue.dueDate
//                    println issue.startDate
//                    println issue.originalEstimateSeconds.toInteger()
                    issue.dueDate = issue.startDate + (issue.originalEstimateSeconds).toInteger().seconds
//                    println issue.dueDate
//
//                    println()
//                    println()
//                    println()
                }
        } else {
            downloadItem = new IssueDownloadItem(issue: issue, property: 'created')
        }

        issueUploadService.enqueue(issue)

        issue.discard()

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

        return timeSpent
    }

}