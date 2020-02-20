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

        if (init)
            init(jobConfig)

        def errorMessage = null
        def milliseconds = null
        try {
            def startTime = new Date()

            timeBoundExecute(body, jobConfig)

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

    def timeBoundExecute(Closure body, SyncJobConfig jobConfig){

        ExecutorService executor = Executors.newFixedThreadPool(4)
        Future<?> future = executor.submit(new Runnable() {
            @Override
            public void run() {
               body(jobConfig)
            }
        })

        executor.shutdown()

        try {
            future.get(10, TimeUnit.MINUTES)
        } catch (InterruptedException e) {
            System.out.println("[JOB] Job was interrupted")
            throw e
        } catch (ExecutionException e) {
            System.out.println("[JOB] Caught exception: " + e.getCause())
            throw e
        } catch (TimeoutException e) {
            future.cancel(true)
            System.out.println("[JOB] Timeout")
            throw e
        }

        if(!executor.awaitTermination(2, TimeUnit.SECONDS)){
            executor.shutdownNow()
        }
    }

    Boolean jobsEnabled(){
        Environment.isDevelopmentMode()
    }
}
