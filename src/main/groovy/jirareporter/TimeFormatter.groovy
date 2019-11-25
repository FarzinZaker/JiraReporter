package jirareporter;

class TimeFormatter {

    static String formatTime(seconds) {
        def secs = Math.round(seconds) ?: 0
        def mins = 0
        def hours = 0
        def days = 0

        mins = ((secs - (secs % 60)) / 60).toInteger()
        secs = (secs % 60).toInteger()

        hours = ((mins - (mins % 60)) / 60).toInteger()
        mins = (mins % 60).toInteger()

        days = ((hours - (hours % 8)) / 8).toInteger()
        hours = (hours % 8).toInteger()

        def time = ''
        if (days > 0) {
            if (time != '')
                time += ' '
            time += "${days}d"
        }
        if (hours > 0) {
            if (time != '')
                time += ' '
            time += "${hours}h"
        }
        if (mins > 0) {
            if (time != '')
                time += ' '
            time += "${mins}m"
        }
        if (secs > 0) {
            if (time != '')
                time += ' '
            time += "${secs}d"
        }

        time
    }
}
