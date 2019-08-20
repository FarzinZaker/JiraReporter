package jirareporter

import grails.converters.JSON
import grails.gorm.transactions.Transactional
import groovy.time.TimeCategory
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClientBuilder
import org.grails.web.json.JSONArray
import org.grails.web.json.JSONObject

@Transactional
class CrossOverService {

    Map getWorkingHours(Date from, Date to, List<String> teams) {
        def logs = CrossOverLog.createCriteria().list {
            gte('date', from)
            lte('date', to)
            if (teams?.size()) {
                'in'('team', teams)
            }
        }
        def data = logs.groupBy { it.name }.each {
            it.value = it.value.groupBy { new Date(it.date.getTime()) }.each {
                it.value = it.value.hours?.find() ?: 0
            }
        }
        def dates = data?.collect { it.value.keySet() }?.flatten()?.unique()
        dates.each { date ->
            data.each { developer ->
                if (!developer.value.containsKey(date))
                    developer.value.put(date, 0)
            }
        }
        data
    }

    void persist(Date from, Date to, List<String> teams) {
        def crossOverData = [:]
        def xoTeams = Configuration.crossOverTeams
        if (teams?.size())
            xoTeams = xoTeams.findAll { teams.contains(it.name) }
        xoTeams.each { crossOverTeam ->
            if (!crossOverData.containsKey(crossOverTeam.name))
                crossOverData.put(crossOverTeam.name, [:])
            def newData = getWorkingHours(crossOverTeam.team, crossOverTeam.manager, from, to)
            newData.keySet().each { developer ->
                if (!crossOverData[crossOverTeam.name].containsKey(developer))
                    crossOverData[crossOverTeam.name].put(developer, [:])
                newData[developer].keySet().each { date ->
                    if (!crossOverData[crossOverTeam.name][developer].containsKey(date))
                        crossOverData[crossOverTeam.name][developer].put(date, 0)
                    crossOverData[crossOverTeam.name][developer][date] += newData[developer][date] ?: 0
                }
            }
        }

        crossOverData.each { team ->
            team.value.each { developer ->
                developer.value.each { date ->
                    def xoLog = CrossOverLog.findByTeamAndNameAndDate(team.key, developer.key, date.key)
                    if (!xoLog)
                        xoLog = new CrossOverLog(team: team.key, name: developer.key, date: date.key)
                    xoLog.hours = date.value
                    xoLog.save()
                }
            }
        }

    }

    Map<String, Map<Date, Double>> getWorkingHours(def teamId, def managerId, Date from, Date to) {

        def result = [:]

//        def maxDate = from

        def date = from - 1

        while (date < to + 1) {
            def dateStr = date.format('yyyy-MM-dd')

            def get = new HttpGet("https://api.crossover.com/api/v2/timetracking/timesheets/assignment?date=${dateStr}&fullTeam=true&managerId=${managerId}&period=WEEK&teamId=${teamId}")
            get.addHeader('Accept', 'application/json, text/plain, */*')
            get.addHeader('Authorization', "Basic ${"${Configuration.crossOverUsername}:${Configuration.crossOverPassowrd}".bytes.encodeBase64().toString()}")
            get.addHeader('Content-Type', 'application/json;charset=UTF-8')
            get.addHeader('Origin', 'https://app.crossover.com')
            get.addHeader('Referer', "https://app.crossover.com/x/dashboard/team/${teamId}/${managerId}/team-timesheet?date=${dateStr}")
            get.addHeader('User-Agent', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.100 Safari/537.36')

            def client = HttpClientBuilder.create().build()
            def response = client.execute(get)

            def bufferedReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()))
            JSONArray data = JSON.parse(bufferedReader.getText())
            data.each { JSONObject record ->
                if (!result.containsKey(record.name))
                    result.put(record.name, [:])
                record.stats.each { JSONObject stat ->
                    def statDate = Date.parse("yyyy-MM-dd'T'hh:mm:ss.000'Z'", stat.date)
                    if (statDate >= from && statDate <= to) {
                        if (!result[record.name].containsKey(statDate))
                            result[record.name].put(statDate, stat.hours)
                    }
//                    if (statDate > maxDate)
//                        maxDate = statDate
                }
            }
            use(TimeCategory) {
                date = date + 1.day
            }
        }
        result
    }
}
