package jirareporter

import grails.converters.JSON

class WorklogController {

    def reportService

    def index(){

    }

    def report() {

        def reportDays = params.id?.toString()?.toInteger() ?: 14

        def details = reportService.getWorklogs(reportDays)

        def summary = [:]
        details.each { worklog ->
            if (!summary.containsKey(worklog.author.displayName))
                summary.put(worklog.author.displayName, [data: worklog.author, timeSpendSeconds: 0])
            summary[worklog.author.displayName]['timeSpendSeconds'] += worklog.timeSpentSeconds
        }

        summary.each { item ->
            def secs = item.value.timeSpendSeconds
            def mins = 0
            def hours = 0
            def days = 0

            mins = ((secs - (secs % 60)) / 60).toInteger()
            secs = (secs % 60).toInteger()

            hours = ((mins - (mins % 60)) / 60).toInteger()
            mins = (mins % 60).toInteger()

            days = ((hours - (hours % 8)) / 8).toInteger()
            hours = (hours % 8).toInteger()

            item.value.timeSpent = ''
            if (days > 0) {
                if (item.value.timeSpent != '')
                    item.value.timeSpent += ' '
                item.value.timeSpent += "${days}d"
            }
            if (hours > 0) {
                if (item.value.timeSpent != '')
                    item.value.timeSpent += ' '
                item.value.timeSpent += "${hours}h"
            }
            if (mins > 0) {
                if (item.value.timeSpent != '')
                    item.value.timeSpent += ' '
                item.value.timeSpent += "${mins}m"
            }
            if (secs > 0) {
                if (item.value.timeSpent != '')
                    item.value.timeSpent += ' '
                item.value.timeSpent += "${secs}d"
            }
        }

        [details: details, summary: summary]
    }

    def reportData() {
        def days = params.id?.toString()?.toInteger()
        render(reportService.getWorklogs(days) as JSON)
    }
}
