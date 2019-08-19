package jirareporter

import grails.gorm.transactions.Transactional

@Transactional
class AccomplishmentsService {

    List<Issue> getTasks(List<Worklog> worklogs) {
        List<Issue> result = []

        worklogs.each { worklog ->
            def task = result.find { it.key == worklog.task.key }
            if (!task) {
                result.add(worklog.task as Issue)
                task = result.find { it.key == worklog.task.key }
                task.assignees = [:]
                task.assignees.put(worklog.author, [worklog])
            } else {
                if (!task.assignees.containsKey(worklog.author))
                    task.assignees.put(worklog.author, [])
                task.assignees[worklog.author] << worklog
            }
        }

        result
    }
}
