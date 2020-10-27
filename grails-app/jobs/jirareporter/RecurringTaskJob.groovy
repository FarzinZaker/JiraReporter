package jirareporter

import grails.util.Environment

class RecurringTaskJob {
    static triggers = {
        simple repeatInterval: 1 * 20 * 1000l // execute job once in 5 seconds
    }

    static concurrent = false

    def recurringTaskService
    def jobExecutionService

    def execute() {

        if (!jobExecutionService.jobsEnabled())
            return

        jobExecutionService.execute('Create Recurring Issues',
                { SyncJobConfig jobConfig, Date startDate, Date endDate, Long lastRecord ->
                    if (!lastRecord)
                        lastRecord = 0
                    RecurringTaskSetting.withTransaction {
                        def setting = RecurringTaskSetting.createCriteria().list {
                            gt('id', lastRecord)
                            eq('enabled', true)
                            order('id', 'asc')
                            maxResults(1)
                        }?.find() as RecurringTaskSetting
                        if (setting)
                            try {
                                if (setting.components?.size())
                                    recurringTaskService.execute(setting)
                                lastRecord = setting.id
                            } catch (ex) {
                                println ex.message
                            }
                        else
                            lastRecord = 0
                    }
                    [
                            startDate : startDate,
                            endDate   : endDate,
                            lastRecord: lastRecord
                    ]
                }

        )
    }

}
