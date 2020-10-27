package jirareporter

import grails.gorm.transactions.Transactional
import grails.util.Environment

import java.util.concurrent.ExecutionException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

@Transactional
class JobExecutionService {

    def execute(String name, Closure body, Closure init = null, Closure finalize = null) {

        def jobConfig = SyncJobConfig.findByName(name)
        if (!jobConfig)
            jobConfig = new SyncJobConfig(name: name).save(flush: true)

        Date startDate = new Date()
        Date endDate = new Date()
        Long lastRecord = jobConfig.lastRecord ?: 0

        if (init) {
            def result = init(jobConfig, startDate, endDate, lastRecord)
            startDate = result.startDate
            endDate = result.endDate
            lastRecord = result.lastRecord
        }

        def errorMessage = null
        def milliseconds = null
        try {
            def startTime = new Date()

            def result = timeBoundExecute(body, jobConfig, startDate, endDate, lastRecord)
            startDate = result.startDate
            endDate = result.endDate
            lastRecord = result.lastRecord

            milliseconds = (new Date().getTime() - startTime.getTime())
        } catch (ex) {
            errorMessage = ex.message
            println(errorMessage)
        }

        jobConfig = SyncJobConfig.findByName(name)
        jobConfig.lastErrorMessage = errorMessage
        jobConfig.executionTime = milliseconds
        jobConfig.lastExecutionDate = new Date()
        if (finalize) {
            def result = finalize(jobConfig, startDate, endDate, lastRecord)
            startDate = result.startDate
            endDate = result.endDate
            lastRecord = result.lastRecord
        }
        jobConfig.startDate = startDate
        jobConfig.endDate = endDate
        jobConfig.lastRecord = lastRecord
        jobConfig.save(flush: true)

    }

    def timeBoundExecute(Closure body, SyncJobConfig jobConfig, Date startDate, Date endDate, Long lastRecord) {

        def result
        ExecutorService executor = Executors.newFixedThreadPool(4)
        Future<?> future = executor.submit(new Runnable() {
            @Override
            public void run() {
                result = body(jobConfig, startDate, endDate, lastRecord)
            }
        })

        executor.shutdown()

        try {
            future.get(200, TimeUnit.MINUTES)
        } catch (InterruptedException e) {
            System.out.println("[JOB] - [${jobConfig?.name}] Job was interrupted")
            throw e
        } catch (ExecutionException e) {
            System.out.println("[JOB] - [${jobConfig?.name}] Caught exception: " + e.getCause())
            throw e
        } catch (TimeoutException e) {
            future.cancel(true)
            System.out.println("[JOB] - [${jobConfig?.name}] Timeout")
            throw e
        }

        if (!executor.awaitTermination(2, TimeUnit.SECONDS)) {
            executor.shutdownNow()
        }
        result
    }

    Boolean jobsEnabled() {
        !Environment.isDevelopmentMode()
    }
}
