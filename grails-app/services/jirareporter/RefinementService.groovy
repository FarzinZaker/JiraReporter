package jirareporter

import grails.gorm.transactions.Transactional

@Transactional
class RefinementService {

    Map getDeveloperSummary(List<Map> worklogs) {

        def summary = [:]

        worklogs.each { worklog ->
            if (!summary.containsKey(worklog.author.displayName))
                summary.put(worklog.author.displayName, 0)
            summary[worklog.author.displayName] += worklog.timeSpentSeconds
        }

        summary
    }

    Map getClientSummary(List<Map> worklogs) {

        def summary = [:]

        worklogs.each { worklog ->
            worklog.task.clients?.each { client ->
                if (!summary.containsKey(client))
                    summary.put(client, 0)
                summary[client] += worklog.timeSpentSeconds
            }
        }

        summary
    }

    Map getProjectSummary(List<Map> worklogs) {

        def summary = [:]

        worklogs.each { worklog ->
            if (!summary.containsKey(worklog.task.project.name))
                summary.put(worklog.task.project.name, 0)
            summary[worklog.task.project.name] += worklog.timeSpentSeconds
        }

        summary
    }

    Map getComponentSummary(List<Map> worklogs) {

        def summary = [:]

        worklogs.each { worklog ->
            worklog.task.components?.each { component ->
                if (!summary.containsKey(component.name))
                    summary.put(component.name, 0)
                summary[component.name] += worklog.timeSpentSeconds
            }
        }

        summary
    }

    Map getIssueTypeSummary(List<Map> worklogs) {

        def summary = [:]

        worklogs.each { worklog ->
            if (!summary.containsKey(worklog.task.issueType.name))
                summary.put(worklog.task.issueType.name, 0)
            summary[worklog.task.issueType.name] += worklog.timeSpentSeconds
        }

        summary
    }

    Map getClientDetails(List<Map> worklogs) {

        def summary = [:]

        worklogs.each { worklog ->
            if (!summary.containsKey(worklog.author.displayName))
                summary.put(worklog.author.displayName, [data: worklog.author, others: [:]])
            worklog.task.clients.each { client ->
                if (!summary[worklog.author.displayName].others.containsKey(client))
                    summary[worklog.author.displayName].others.put(client, [timeSpendSeconds: 0])
                summary[worklog.author.displayName].others[client].timeSpendSeconds += worklog.timeSpentSeconds
            }
        }

        summary.each { parent ->
            summary[parent.key].others.each { item ->

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
        }

        summary
    }

    Map getComponentDetails(List<Map> worklogs) {

        def summary = [:]

        worklogs.each { worklog ->
            if (!summary.containsKey(worklog.author.displayName))
                summary.put(worklog.author.displayName, [data: worklog.author, others: [:]])
            worklog.task.components.each { component ->
                if (!summary[worklog.author.displayName].others.containsKey(component.name))
                    summary[worklog.author.displayName].others.put(component.name, [timeSpendSeconds: 0])
                summary[worklog.author.displayName].others[component.name].timeSpendSeconds += worklog.timeSpentSeconds
            }
        }

        summary.each { parent ->
            summary[parent.key].others.each { item ->

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
        }

        summary
    }

    Map getProjectDetails(List<Map> worklogs) {

        def summary = [:]

        worklogs.each { worklog ->
            if (!summary.containsKey(worklog.author.displayName))
                summary.put(worklog.author.displayName, [data: worklog.author, others: [:]])
            if (!summary[worklog.author.displayName].others.containsKey(worklog.project.name))
                summary[worklog.author.displayName].others.put(worklog.project.name, [timeSpendSeconds: 0])
            summary[worklog.author.displayName].others[worklog.project.name].timeSpendSeconds += worklog.timeSpentSeconds
        }

        summary.each { parent ->
            summary[parent.key].others.each { item ->

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
        }

        summary
    }

    Map getIssueTypeDetails(List<Map> worklogs) {

        def summary = [:]

        worklogs.each { worklog ->
            if (!summary.containsKey(worklog.author.displayName))
                summary.put(worklog.author.displayName, [data: worklog.author, others: [:]])
            if (!summary[worklog.author.displayName].others.containsKey(worklog.task.issueType.name))
                summary[worklog.author.displayName].others.put(worklog.task.issueType.name, [timeSpendSeconds: 0])
            summary[worklog.author.displayName].others[worklog.task.issueType.name].timeSpendSeconds += worklog.timeSpentSeconds
        }

        summary.each { parent ->
            summary[parent.key].others.each { item ->

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
                    item.value.timeSpent += "${secs}s"
                }
            }
        }

        summary
    }
}
