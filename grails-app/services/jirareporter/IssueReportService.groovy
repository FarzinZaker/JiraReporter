package jirareporter

import grails.gorm.transactions.Transactional

@Transactional
class IssueReportService {

    def springSecurityService

    List<Issue> getIssues(List<Issue> issues = [], List<Project> projects = [], List<IssueType> issueTypes = [], List<IssueType> priorities = [], List<Component> componentList = [], List<Client> clientList = [], List<JiraUser> users = [], List<JiraUser> teamMembers = [], Boolean filterTeamMembers, List<Status> statusList = []) {

        def loggedInUser = springSecurityService.authentication.principal.username
        def jiraUser = JiraUser.findByName(loggedInUser)

        Issue.createCriteria().list {

            isNotNull('originalEstimate')
            isNotNull('startDate')
            isNotNull('dueDate')

            if (![Roles.MANAGER, Roles.ADMIN].any { springSecurityService.authentication.authorities.contains(it) } && jiraUser) {
                eq('assignee', jiraUser)
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

            if (users.size()) {
                'in'('assignee', users)
            }

            if (filterTeamMembers) {
                'in'('assignee', teamMembers + [null])
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
        } as List<Issue>
    }

    List<Issue> findChildIssues(List<Issue> issues) {
        if (!issues?.size())
            return []
        issues + findChildIssues(
                (Issue.findAllByParentInList(issues) +
                        IssueLink.findAllByDeletedAndSecondIssueInListAndType(false, issues, 'is child of').firstIssue +
                        IssueLink.findAllByDeletedAndFirstIssueInListAndType(false, issues, 'is parent of').secondIssue)
                        .unique { it.id })

    }
}
