package jirareporter

class RecurringTaskJob {
    static triggers = {
        simple repeatInterval: 10 * 60 * 1000l // execute job once in 5 seconds
    }

    static concurrent = false

    def recurringTaskService

    def execute() {
        RecurringTaskSetting.findAllByEnabled(true).each { setting ->
            recurringTaskService.execute(setting)
        }
    }
}
