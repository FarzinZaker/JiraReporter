package jirareporter

import grails.converters.JSON
import grails.gorm.transactions.Transactional

import java.text.SimpleDateFormat

@Transactional
class IntegrityService {

    def crossOverService

    def getDeveloperIntegritySummary(List<Map> worklogs, Date from, Date to) {

        def summary = [:]

        def dates = new HashSet()

        def minDate = from
        def maxDate = to

        def crossOverData = [:]
        Configuration.crossOverTeams.each { crossOverTeam ->
            crossOverData.putAll(crossOverService.getWorkingHours(crossOverTeam.team, crossOverTeam.manager, minDate, maxDate))
        }

        worklogs.each { worklog ->
            def developer = worklog.author.displayName
            if (crossOverData.containsKey(developer)) {
                def date = worklog.started.clearTime()
                dates << date

                if (!summary.containsKey(developer))
                    summary.put(developer, [:])
                if (!summary[developer].containsKey(date))
                    summary[developer].put(date, [:])
                if (!summary[developer][date].containsKey('jiraTime'))
                    summary[developer][date].put('jiraTime', 0)
                summary[developer][date]['jiraTime'] += worklog.timeSpentSeconds

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
                if (summary.containsKey(developer)) {
                    if (!summary[developer].containsKey(date))
                        summary[developer].put(date, [:])
                    if (!summary[developer][date].containsKey('crossOverTime'))
                        summary[developer][date].put('crossOverTime', 0)
                    summary[developer][date]['crossOverTime'] += crossOverData[developer][realDate] * 3600
                }
            }
        }

        summary.keySet().each { developer ->
            summary[developer].keySet().each { date ->
                if (!summary[developer][date]['jiraTime'])
                    summary[developer][date].put('jiraTime', 0)
                try {
                    summary[developer][date].put('difference', summary[developer][date]['jiraTime'] - summary[developer][date]['crossOverTime'])
                    summary[developer][date].put('differencePercent', Math.round(summary[developer][date]['difference'] * 100 / summary[developer][date]['crossOverTime']))
                } catch (Exception ignored) {
                    println([date, developer] as JSON)
                    println(summary[developer][date] as JSON)
                }
            }
        }


        summary.keySet().each { developer ->
            summary[developer].keySet().each { date ->
                summary[developer][date]['jira'] = TimeFormatter.formatTime(summary[developer][date]['jiraTime'])
                summary[developer][date]['crossOver'] = TimeFormatter.formatTime(summary[developer][date]['crossOverTime'])
            }
        }

        [
                data      : summary,
                developers: summary.keySet(),
                dates     : dates
        ]
    }
}
