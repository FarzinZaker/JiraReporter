package jirareporter

import grails.converters.JSON
import grails.gorm.transactions.Transactional

import java.text.SimpleDateFormat

@Transactional
class IntegrityService {

    def crossOverService

    def getDeveloperIntegritySummary(List<Map> worklogs, Date from, Date to) {

        def dailySummary = [:]
        def totalSummary = [:]

        def dates = new HashSet()

        def minDate = from
        def maxDate = to

        def crossOverData = [:]
        Configuration.crossOverTeams.each { crossOverTeam ->
            def newData = crossOverService.getWorkingHours(crossOverTeam.team, crossOverTeam.manager, minDate, maxDate)
            newData.keySet().each { developer ->
                if (!crossOverData.containsKey(developer))
                    crossOverData.put(developer, [:])
                newData[developer].keySet().each { date ->
                    if (!crossOverData[developer].containsKey(date))
                        crossOverData[developer].put(date, 0)
                    crossOverData[developer][date] += newData[developer][date] ?: 0
                }
            }
        }

        worklogs.each { worklog ->
            def developer = worklog.author.displayName
            if (crossOverData.containsKey(developer)) {
                def date = worklog.started.clearTime()
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
