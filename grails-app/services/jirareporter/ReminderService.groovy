package jirareporter

import grails.gorm.transactions.Transactional

import java.text.SimpleDateFormat

@Transactional
class ReminderService {

    def slackService
    def queryService
    def issueDownloadService

    def sendReminders() {
        def formatter = new SimpleDateFormat('yyyy-MM-dd')
        def time = formatter.format(new Date())
        Reminder.list().each { reminder ->
            JiraUser.findAllBySlackIdIsNotNull().each { user ->

                def message = "```${reminder.name}: ${time}```\n"

                def query = reminder.query.replace('[user]', user.name)

                def result = queryService.execute(query)
                if (result.total) {
                    def list = result.issues.collect {
                        issueDownloadService.download(it.key)
                        Issue.findByKey(it.key)
                    }

                    def issues = ''
                    def priorities = list.collect{it.priority}.unique()

                    priorities.each {priority ->
                        def priorityIssues = []

                        def statusList = list.collect{it.status}.unique()
                        statusList.each{status ->

                            def items = list.findAll{it.priority == priority && it.status == status}.collect{
                                ">• ${it.parent?"<https://jira.devfactory.com/browse/${it.parent?.key}|${it.parent?.key}> - ${it.parent?.summary} / ":''}<https://jira.devfactory.com/browse/${it.key}|${it.key}> - ${it.summary}\n"}

                            if(items.size()) {
                                priorityIssues << "_${status.name}_\n" + items?.join('')
                            }
                        }

                        if(priorityIssues?.size()) {
                            issues += "\n*${priority.name} Priority*\n>"
                            issues += priorityIssues.join('>\n>') + '\n'
                        }
                    }
                    message += reminder.template.replace('[items]', issues)
                } else {
                    message += '\n>' + reminder.emptyMessage
                }

                println message
                slackService.post(user.slackId, message)
            }
        }
    }
}
