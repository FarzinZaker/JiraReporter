package jirareporter

import grails.gorm.transactions.Transactional

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
            list.addAll(Configuration.statusList.find { it.name == st }.details)
        }
        Status.findAllByNameInList(list + ['-'])
    }

    List<String> formatTeams(params) {
        params.team?.split(',')?.collect { it.toString()?.trim() }?.findAll { it } ?: []
    }
}
