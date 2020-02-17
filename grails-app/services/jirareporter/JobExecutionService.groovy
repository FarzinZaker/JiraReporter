package jirareporter

import grails.gorm.transactions.Transactional

@Transactional
class JobExecutionService {

    def execute(String name, Closure body, Closure init = null, Closure finalize = null) {

        def jobConfig = SyncJobConfig.findByName(name)
        if (!jobConfig)
            jobConfig = new SyncJobConfig(name: name).save(flush: true)

        if (init)
            init(jobConfig)

        def errorMessage = null
        def milliseconds = null
        try {
            def startTime = new Date()

            body(jobConfig)

            milliseconds = (new Date().getTime() - startTime.getTime())
        } catch (ex) {
            errorMessage = ex.message
            println(errorMessage)
        }

        jobConfig = SyncJobConfig.findByName(name)
        jobConfig.lastErrorMessage = errorMessage
        jobConfig.executionTime = milliseconds
        jobConfig.lastExecutionDate = new Date()
        if (finalize)
            finalize(jobConfig)
        jobConfig.save(flush: true)

    }
}
