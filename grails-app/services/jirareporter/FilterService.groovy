package jirareporter

import grails.gorm.transactions.Transactional
import groovy.time.TimeCategory

@Transactional
class FilterService {

    List<JiraUser> formatUsersList(params) {
        JiraUser.findAllByDisplayNameInList((params.user?.split(',')?.collect {
            it.split('\\(')?.first()?.replace(')', '')?.trim()
        }?.findAll { it } ?: []) + ['-'])
    }

    List<Issue> formatIssueList(params) {
        Issue.findAllByKeyInList((params.issue?.split(',')?.collect {
            it.split(':')?.first()?.trim()
        }?.findAll { it } ?: []) + ['-'])
    }

    List<JiraUser> formatTeamMembers(Set<String> users) {
        JiraUser.findAllByDisplayNameInList((users?.toList() ?: []) + ['-'])
    }

    List<String> formatWorklogTypes(params) {
        params.worklogTypes?.split(',')?.collect { it.split('\\(')?.last()?.replace(')', '')?.trim() }?.findAll {
            it
        } ?: []
    }

    List<Project> formatProjects(params) {
        Project.findAllByKeyInList((params.project?.split(',')?.collect { it.toString()?.trim() }?.findAll {
            it
        } ?: []) + ['-'])
    }

    List<IssueType> formatIssueTypes(params) {
        IssueType.findAllByNameInList((params.issueType?.split(',')?.collect { it.toString()?.trim() }?.findAll {
            it
        } ?: []) + ['-'])
    }

    List<Priority> formatPriorities(params) {
        Priority.findAllByNameInList((params.priority?.split(',')?.collect { it.toString()?.trim() }?.findAll {
            it
        } ?: []) + ['-'])
    }

    List<Component> formatComponents(params) {
        Component.findAllByNameInList((params.component?.split(',')?.collect { it.toString()?.trim() }?.findAll {
            it
        } ?: []) + ['-'])
    }

    List<Client> formatClients(params) {
        Client.findAllByNameInList((params.client?.split(',')?.collect { it.toString()?.trim() }?.findAll {
            it
        } ?: []) + ['-'])
    }

    List<Status> formatStatus(params) {
        def list = []
        params.status?.split(',')?.collect { it.toString()?.trim() }?.findAll { it }?.each { st ->
            list.addAll(Configuration.statusList.find { it.name == st }?.details)
        }.findAll { it }
        Status.findAllByNameInList(list + ['-'])
    }

    List<Team> formatTeams(params) {
        if (params.team && params.team?.trim() != '')
            Team.findAllByIdInList((params.team?.split(',')?.collect { it.toString()?.toLong() }?.findAll {
                it
            } ?: []) + [null])
        else
            []
    }

    Boolean formatUnassigned(params) {
        params.unassigned ? true : false
    }

    Date formatFromDate(params) {
        if (params.to == null)
            return null
        formatDate(params.from)
    }

    Date formatToDate(params) {
        if (params.to == null)
            return null
        def date = formatDate(params.to)
        date ? date + 1 : null
    }

    Date formatDate(String input) {
        if (input.contains('/'))
            new Date(input).clearTime()
        else
            formatRelativeDate(input).clearTime()
    }

    Date formatRelativeDate(String input) {
        input = input.trim().toLowerCase()
        if (input == 'today')
            return new Date().clearTime()
        def scale = 'days'
        if (input.endsWith('d')) {
            scale = 'weeks'
            input = input.substring(0, input.size() - 1).trim()
        } else if (input.endsWith('w')) {
            scale = 'weeks'
            input = input.substring(0, input.size() - 1).trim()
        } else if (input.endsWith('m')) {
            scale = 'months'
            input = input.substring(0, input.size() - 1).trim()
        } else if (input.endsWith('y')) {
            scale = 'years'
            input = input.substring(0, input.size() - 1).trim()
        }
        def multiplier = 1
        if (input.startsWith('-')) {
            multiplier = -1
            input = input.substring(1)
        }
        try {
            def amount = input.toInteger()
            def date = new Date().clearTime()
            use(TimeCategory) {
                date = date + (multiplier * amount)."${scale}"
            }
        }
        catch (ignore) {
            return null
        }
    }
}
