package jirareporter

import grails.converters.JSON
import grails.gorm.transactions.Transactional

@Transactional
class IntegrityService {

    def getDeveloperIntegritySummary(List<Worklog> worklogs, Date from, Date to, Map crossOverData) {

        def dailySummary = [:]
        def totalSummary = [:]

        def dates = new HashSet()

        def minDate = from
        def maxDate = to

        worklogs.each { worklog ->
            def developer = worklog.author.displayName
            if (crossOverData.containsKey(developer)) {
                def date = new Date(worklog.started.clearTime().getTime())
                dates << date

                if (!totalSummary.containsKey(developer))
                    totalSummary.put(developer, [:])
                if (!totalSummary[developer].containsKey('jiraTime'))
                    totalSummary[developer].put('jiraTime', 0)

                if (!dailySummary.containsKey(developer))
                    dailySummary.put(developer, [:])
                if (!dailySummary[developer].containsKey(date))
                    dailySummary[developer].put(date, [:])
                if (!dailySummary[developer][date].containsKey('jiraTime'))
                    dailySummary[developer][date].put('jiraTime', 0)

                totalSummary[developer]['jiraTime'] += worklog.timeSpentSeconds
                dailySummary[developer][date]['jiraTime'] += worklog.timeSpentSeconds

                if (worklog.started < minDate)
                    minDate = worklog.started

                if (worklog.started > maxDate)
                    maxDate = worklog.started
            }
        }
        crossOverData.keySet().each { developer ->
            crossOverData[developer].keySet().each { realDate ->
                def date = realDate.clearTime()
                dates << date
                if (dailySummary.containsKey(developer)) {

                    if (!totalSummary[developer].containsKey('crossOverTime'))
                        totalSummary[developer].put('crossOverTime', 0)

                    if (!dailySummary[developer].containsKey(date))
                        dailySummary[developer].put(date, [:])
                    if (!dailySummary[developer][date].containsKey('crossOverTime'))
                        dailySummary[developer][date].put('crossOverTime', 0)

                    totalSummary[developer]['crossOverTime'] += crossOverData[developer][realDate] * 3600
                    dailySummary[developer][date]['crossOverTime'] += crossOverData[developer][realDate] * 3600
                }
            }
        }

        dailySummary.keySet().each { developer ->
            dailySummary[developer].keySet().each { date ->

                if (!totalSummary[developer]['jiraTime'])
                    totalSummary[developer].put('jiraTime', 0)
                if (!totalSummary[developer]['crossOverTime'])
                    totalSummary[developer].put('crossOverTime', 0)

                if (!dailySummary[developer][date]['jiraTime'])
                    dailySummary[developer][date].put('jiraTime', 0)
                if (!dailySummary[developer][date]['crossOverTime'])
                    dailySummary[developer][date].put('crossOverTime', 0)

                try {
                    dailySummary[developer][date].put('difference', dailySummary[developer][date]['jiraTime'] - dailySummary[developer][date]['crossOverTime'])
                    dailySummary[developer][date].put('differencePercent', Math.round(dailySummary[developer][date]['difference'] * 100 / (dailySummary[developer][date]['crossOverTime'] ?: 1)))
                } catch (Exception ignored) {
                    println([date, developer] as JSON)
                    println(dailySummary[developer][date] as JSON)
                }
            }
        }


        dailySummary.keySet().each { developer ->
            dailySummary[developer].keySet().each { date ->
                dailySummary[developer][date]['jira'] = TimeFormatter.formatTime(dailySummary[developer][date]['jiraTime'])
                dailySummary[developer][date]['crossOver'] = TimeFormatter.formatTime(dailySummary[developer][date]['crossOverTime'])
            }
        }

        [
                daily: [
                        data      : dailySummary,
                        developers: dailySummary.keySet(),
                        dates     : dates
                ],
                total: totalSummary
        ]
    }
}
