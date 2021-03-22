package jirareporter

class ReminderJob {
    static triggers = {
        cron name: 'myTrigger', cronExpression: "0 0 12 ? * MON-FRI"
    }

    def reminderService
    def jobExecutionService

    def execute() {

        println "Reminder Job executed"

        if (!jobExecutionService.jobsEnabled())
            return

        reminderService.sendReminders()
    }
}
