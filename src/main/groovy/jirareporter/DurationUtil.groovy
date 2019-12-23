package jirareporter

class DurationUtil {

    static Long getDurationSeconds(String duration) {
        if (!duration)
            return 0

        def seconds = 0
        def parts = duration.split(' ').collect { it.trim() }.findAll { it && it != '' }
        parts.each { dur ->
            if (dur.endsWith('w'))
                seconds += dur.replace('w', '').toInteger() * 60 * 60 * 8 * 5
            else if (dur.endsWith('d'))
                seconds += dur.replace('d', '').toInteger() * 60 * 60 * 8
            else if (dur.endsWith('h'))
                seconds += dur.replace('h', '').toInteger() * 60 * 60
            else
                seconds += dur.replace('m', '').toInteger() * 60
        }
        seconds
    }

    static String formatDuration(Long time) {
        def secs = time
        def mins = 0
        def hours = 0
        def days = 0

        mins = ((secs - (secs % 60)) / 60).toInteger()
        secs = (secs % 60).toInteger()

        hours = ((mins - (mins % 60)) / 60).toInteger()
        mins = (mins % 60).toInteger()

        days = ((hours - (hours % 8)) / 8).toInteger()
        hours = (hours % 8).toInteger()

        def timeSpent = ''
        if (days > 0) {
            if (timeSpent != '')
                timeSpent += ' '
            timeSpent += days + 'd'
        }
        if (hours > 0) {
            if (timeSpent != '')
                timeSpent += ' '
            timeSpent += hours + 'h'
        }
        if (mins > 0) {
            if (timeSpent != '')
                timeSpent += ' '
            timeSpent += mins + 'm'
        }

        if (timeSpent == '')
            timeSpent = '0'

        return timeSpent
    }
}
