package jirareporter

import grails.gorm.transactions.Transactional

@Transactional
class IssueReportService {

    List<Issue> getIssues(List<Project> projects = [], List<IssueType> issueTypes = [], List<IssueType> priorities = [], List<Component> componentList = [], List<Client> clientList = [], List<JiraUser> users = [], List<JiraUser> teamMembers = [], Boolean filterTeamMembers, List<Status> statusList = []) {

        println 'Started Query'
        Issue.createCriteria().list {

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

}
