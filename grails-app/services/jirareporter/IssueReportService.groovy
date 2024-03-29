package jirareporter

import grails.gorm.transactions.Transactional

@Transactional
class IssueReportService {

    def springSecurityService

    List<Issue> getIssues(Date from, Date to, List<Issue> issues = [], List<IssueProduct> products = [], List<Project> projects = [], List<IssueType> issueTypes = [], List<IssueType> priorities = [], List<Component> componentList = [], List<Client> clientList = [], List<Label> labelList = [], List<JiraUser> users = [], List<JiraUser> teamMembers = [], Boolean filterTeamMembers, List<Status> statusList = [], Boolean noRecurring = false, Boolean unassignedIssues = false) {

        def user = User.findByUsername(springSecurityService.principal.username)
        def jiraUsers = [JiraUser.findByName(user.username)]
        if (springSecurityService.authentication.authorities.collect { it.role }.contains(Roles.MANAGER)) {
            TeamManager.findAllByManager(user).collect { it.team }.each { team ->
                JiraUser.findAllByTeam(team).each { jUser ->
                    jiraUsers << jUser
                }
            }
        }

        Issue.createCriteria().list {

//            isNotNull('originalEstimate')
//            isNotNull('startDate')
//            isNotNull('dueDate')
            eq('deleted', false)

            if (from) {
                or {
                    gte('startDate', from)
                    gte('dueDate', from)
                }
            }

            if (to) {
                or {
                    lte('startDate', to)
                    lte('dueDate', to)
                }
            }

            if (![Roles.ADMIN].any {
                springSecurityService.authentication.authorities.collect { it.role }.contains(it)
            } && jiraUsers?.size()) {
                if (unassignedIssues) {
                    or {
                        'in'('assignee', jiraUsers)
                        isNull('assignee')
                    }
                } else {
                    'in'('assignee', jiraUsers)
                }
            }

            if (issues.size()) {
//                or {
                'in'('id', findChildIssues(issues).collect { it.id })
//                    'in'('parent', issues)
//                }
            }

            if (projects.size()) {
                'in'('project', projects)
            }

            if (products.size()) {
                'in'('product', products)
            }

            if (users.size()) {
                if (unassignedIssues) {
                    or {
                        'in'('assignee', users)
                        isNull('assignee')
                    }
                } else {
                    'in'('assignee', users)
                }
            }

            if (filterTeamMembers) {
                if (unassignedIssues) {
                    or {
                        'in'('assignee', teamMembers + [null])
                        isNull('assignee')
                    }
                } else {
                    'in'('assignee', teamMembers + [null])
                }
            }

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
            if (labelList.size()) {
                labels {
                    'in'('id', labelList.collect { it.id })
                }
            }
        }.unique { it.key }.findAll { !noRecurring || !it.labels?.any { it.name == 'Recurring-Task' } } as List<Issue>
    }

    List<Issue> findChildIssues(List<Issue> issues) {
        if (!issues?.size())
            return []
        issues + findChildIssues(
                (Issue.findAllByParentInList(issues) +
                        IssueLink.findAllByDeletedAndSecondIssueInListAndTypeInList(false, issues, ['is child of', 'Feature Epic Link']).firstIssue +
                        IssueLink.findAllByDeletedAndFirstIssueInListAndTypeInList(false, issues, ['is parent of', 'Epic is Linked to']).secondIssue)
                        .unique { it.id })

    }
}
