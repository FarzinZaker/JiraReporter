package jirareporter

import grails.gorm.transactions.Transactional

@Transactional
class RefinementService {

    Map getDeveloperSummary(List<Worklog> worklogs) {

        def summary = [:]

        worklogs.each { worklog ->
            if (!summary.containsKey(worklog.author.displayName))
                summary.put(worklog.author.displayName, [timeSpent: 0, tasks: new HashSet<String>()])
            summary[worklog.author.displayName].timeSpent += worklog.timeSpentSeconds
            summary[worklog.author.displayName].tasks.add(worklog.issueId)
        }

        summary.each { item ->
            item.value.tasksCount = item.value.tasks.size()
        }

        summary
    }

    Map getClientSummary(List<Worklog> worklogs) {

        def summary = [:]

        worklogs.each { worklog ->
            worklog.task.clients?.each { client ->
                if (!summary.containsKey(client))
                    summary.put(client, [timeSpent: 0, tasks: new HashSet<String>()])
                summary[client].timeSpent += worklog.timeSpentSeconds
                summary[client].tasks.add(worklog.issueId)
            }
        }

        summary.each { item ->
            item.value.tasksCount = item.value.tasks.size()
        }

        summary
    }

    Map getProjectSummary(List<Worklog> worklogs) {

        def summary = [:]

        worklogs.each { worklog ->
            if (!summary.containsKey(worklog.task.project.name))
                summary.put(worklog.task.project.name, [timeSpent: 0, tasks: new HashSet<String>()])
            summary[worklog.task.project.name].timeSpent += worklog.timeSpentSeconds
            summary[worklog.task.project.name].tasks.add(worklog.issueId)
        }

        summary.each { item ->
            item.value.tasksCount = item.value.tasks.size()
        }

        summary
    }

    Map getComponentSummary(List<Worklog> worklogs) {

        def summary = [:]

        worklogs.each { worklog ->
            worklog.task.components?.each { component ->
                if (!summary.containsKey(component.name))
                    summary.put(component.name, [timeSpent: 0, tasks: new HashSet<String>()])
                summary[component.name].timeSpent += worklog.timeSpentSeconds
                summary[component.name].tasks.add(worklog.issueId)
            }
        }

        summary.each { item ->
            item.value.tasksCount = item.value.tasks.size()
        }

        summary
    }

    Map getIssueTypeSummary(List<Worklog> worklogs) {

        def summary = [:]

        worklogs.each { worklog ->
            if (!summary.containsKey(worklog.task.issueType.name))
                summary.put(worklog.task.issueType.name, [timeSpent: 0, tasks: new HashSet<String>()])
            summary[worklog.task.issueType.name].timeSpent += worklog.timeSpentSeconds
            summary[worklog.task.issueType.name].tasks.add(worklog.issueId)
        }

        summary.each { item ->
            item.value.tasksCount = item.value.tasks.size()
        }

        summary
    }

    Map getClientDetails(List<Worklog> worklogs) {

        def summary = [:]

        worklogs.each { worklog ->
            if (!summary.containsKey(worklog.author.displayName))
                summary.put(worklog.author.displayName, [data: worklog.author, others: [:]])
            worklog.task.clients.each { client ->
                if (!summary[worklog.author.displayName].others.containsKey(client))
                    summary[worklog.author.displayName].others.put(client, [timeSpendSeconds: 0, tasks: new HashSet<String>()])
                summary[worklog.author.displayName].others[client].timeSpendSeconds += worklog.timeSpentSeconds
                summary[worklog.author.displayName].others[client].tasks.add(worklog.issueId)
            }
        }

        summary.each { parent ->
            summary[parent.key].others.each { item ->
                item.value.timeSpent = TimeFormatter.formatTime(item.value.timeSpendSeconds)
                item.value.tasksCount = item.value.tasks.size()
            }
        }

        summary
    }

    Map getComponentDetails(List<Worklog> worklogs) {

        def summary = [:]

        worklogs.each { worklog ->
            if (!summary.containsKey(worklog.author.displayName))
                summary.put(worklog.author.displayName, [data: worklog.author, others: [:]])
            worklog.task.components.each { component ->
                if (!summary[worklog.author.displayName].others.containsKey(component.name))
                    summary[worklog.author.displayName].others.put(component.name, [timeSpendSeconds: 0, tasks: new HashSet<String>()])
                summary[worklog.author.displayName].others[component.name].timeSpendSeconds += worklog.timeSpentSeconds
                summary[worklog.author.displayName].others[component.name].tasks.add(worklog.issueId)
            }
        }

        summary.each { parent ->
            summary[parent.key].others.each { item ->
                item.value.timeSpent = TimeFormatter.formatTime(item.value.timeSpendSeconds)
                item.value.tasksCount = item.value.tasks.size()
            }
        }

        summary
    }

    Map getProjectDetails(List<Worklog> worklogs) {

        def summary = [:]

        worklogs.each { worklog ->
            if (!summary.containsKey(worklog.author.displayName))
                summary.put(worklog.author.displayName, [data: worklog.author, others: [:]])
            if (!summary[worklog.author.displayName].others.containsKey(worklog.project.name))
                summary[worklog.author.displayName].others.put(worklog.project.name, [timeSpendSeconds: 0, tasks: new HashSet<String>()])
            summary[worklog.author.displayName].others[worklog.project.name].timeSpendSeconds += worklog.timeSpentSeconds
            summary[worklog.author.displayName].others[worklog.project.name].tasks.add(worklog.issueId)
        }

        summary.each { parent ->
            summary[parent.key].others.each { item ->
                item.value.timeSpent = TimeFormatter.formatTime(item.value.timeSpendSeconds)
                item.value.tasksCount = item.value.tasks.size()
            }
        }

        summary
    }

    Map getIssueTypeDetails(List<Worklog> worklogs) {

        def summary = [:]

        worklogs.each { worklog ->
            if (!summary.containsKey(worklog.author.displayName))
                summary.put(worklog.author.displayName, [data: worklog.author, others: [:]])
            if (!summary[worklog.author.displayName].others.containsKey(worklog.task.issueType.name))
                summary[worklog.author.displayName].others.put(worklog.task.issueType.name, [timeSpendSeconds: 0, tasks: new HashSet<String>()])
            summary[worklog.author.displayName].others[worklog.task.issueType.name].timeSpendSeconds += worklog.timeSpentSeconds
            summary[worklog.author.displayName].others[worklog.task.issueType.name].tasks.add(worklog.issueId)
        }

        summary.each { parent ->
            summary[parent.key].others.each { item ->
                item.value.timeSpent = TimeFormatter.formatTime(item.value.timeSpendSeconds)
                item.value.tasksCount = item.value.tasks.size()
            }
        }

        summary
    }


}
