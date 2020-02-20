package jirareporter

import grails.util.Environment

class RecurringTaskJob {
    static triggers = {
        simple repeatInterval: 1 * 60 * 1000l // execute job once in 5 seconds
    }

    static concurrent = false

    def recurringTaskService
    def jobExecutionService

    def execute() {

        if (!jobExecutionService.jobsEnabled())
            return

        jobExecutionService.execute('Create Recurring Issues',
                { SyncJobConfig jobConfig ->
                    RecurringTaskSetting.findAllByEnabled(true).each { setting ->
                        try {
                            recurringTaskService.execute(setting)
                        } catch (ex) {
                            println ex.message
                        }
                    }
                })
    }
}
