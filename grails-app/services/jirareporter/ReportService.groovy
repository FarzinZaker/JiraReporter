package jirareporter


import grails.gorm.transactions.Transactional

@Transactional
class ReportService {

    def queryService
    def worklogService
    def issueService
    def cacheService

    final String defaultProjectsList = Configuration.projects.collect { it.key }.join(',')
    final String defaultIssueTypeList = Configuration.issueTypes.collect { "\"${it}\"" }.join(',')

    List<Worklog> getWorklogs(Date from, Date to, List<Project> projects = [], List<IssueType> issueTypes = [], List<Priority> priorities = [], List<Component> componentList = [], List<Client> clientList = [], List<JiraUser> users = [], List<JiraUser> teamMembers = [], Boolean filterTeamMembers, List<String> worklogTypes = [], List<Status> statusList = []) {

        println 'Started Query'
        Worklog.createCriteria().list {
            gte('started', from)
            lte('started', to)

            if (projects.size()) {
                'in'('project', projects)
            }

            if (users.size()) {
                'in'('author', users)
            }

            if (filterTeamMembers) {
                'in'('author', teamMembers + [null])
            }

            if (worklogTypes.contains('billable') && !worklogTypes.contains('non-billable')) {
                ilike('comment', '%[billable]%')
            }

            if (componentList.size() || clientList.size() || issueTypes.size() || statusList.size() || priorities.size()) {
                task {

                    if (issueTypes.size()) {
                        'in'('issueType', issueTypes)
                    }

                    if (priorities.size()) {
                        'in'('priority', priorities)
                    }

                    if (statusList.size()) {
                        'in'('status', statusList)
                    }

                    if (componentList.size()) {
                        components {
                            'in'('id', componentList.collect { it.id })
                        }
                    }
                    if (clientList.size()) {
                        clients {
                            'in'('id', clientList.collect { it.id })
                        }
                    }
                }
            }
        } as List<Worklog>
    }

    List<Worklog> filterComponents(List<Worklog> list, List<String> components) {
        if (!components || components?.size() == 0)
            return list
        list?.findAll {
            def result = false
            it.task.components.each { component ->
                if (components.contains(component.name))
                    result = true
            }
            result
        } ?: []
    }

    List<Worklog> filterClients(List<Worklog> list, List<String> clients) {
        if (!clients || clients?.size() == 0)
            return list
        list?.findAll {
            def result = false
            it.task.clients.each { client ->
                if (clients.contains(client?.toString()?.toLowerCase()?.trim()))
                    result = true
            }
            result
        } ?: []
    }

    List<Worklog> filterStatus(List<Worklog> list, List<String> statusList) {
        if (!statusList || statusList?.size() == 0)
            return list
        list?.findAll { worklog ->
            statusList.any { status ->
                Configuration.statusList.find { it.name.toUpperCase() == status }?.details?.any {
                    it == worklog.task.status.name?.toUpperCase()
                }
            }
        } ?: []
    }

    List<Worklog> filterWorklogTypes(List<Worklog> list, List<String> worklogTypes) {
        if (!worklogTypes || worklogTypes?.size() == 0)
            return list
        def result = []
        if (worklogTypes.contains('billable'))
            result.addAll(list?.findAll {
                it.comment.contains('[BILLABLE]') || it.comment.contains('[billable]') || it.comment.contains('[Billable]')
            } ?: [])
        if (worklogTypes.contains('non-billable'))
            result.addAll(list?.findAll {
                !(it.comment.contains('[BILLABLE]') || it.comment.contains('[billable]') || it.comment.contains('[Billable]'))
            } ?: [])
        result
    }
}
