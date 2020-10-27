package jirareporter

import sun.misc.GC

class MemoryMonitoringJob {
    static triggers = {
        simple repeatInterval: 5 * 60 * 1000l // execute job once in 5 seconds
    }

    def execute() {

        System.gc()
        // execute job
        int mb = 1024 * 1024

        //Getting the runtime reference from system
        Runtime runtime = Runtime.getRuntime()

        println()
        println()
        println("--------------------------------------------")
        println("##### Heap utilization statistics [MB] #####")
        println("--------------------------------------------")

        //Print used memory
        println("Used Memory:\t"
                + Math.round((runtime.totalMemory() - runtime.freeMemory()) / mb) + "MB")

        //Print free memory
        println("Free Memory:\t"
                + Math.round(runtime.freeMemory() / mb) + "MB")

        //Print total available memory
        println("Total Memory:\t" + Math.round(runtime.totalMemory() / mb) + "MB")

        //Print Maximum available memory
        println("Max Memory:\t\t" + Math.round(runtime.maxMemory() / mb) + "MB")
        println("--------------------------------------------")
        println()
        println()
    }
}
