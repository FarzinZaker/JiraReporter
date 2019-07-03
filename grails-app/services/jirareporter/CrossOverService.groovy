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

    Map<String, Map<Date, Double>> getWorkingHours(def teamId, def managerId, Date from, Date to) {

        def result = [:]

        def maxDate = from

        def date = from - 1

        while (maxDate < to + 1) {
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
                    if (statDate > maxDate)
                        maxDate = statDate
                }
            }
            use(TimeCategory) {
                date = maxDate + 1.day
            }
        }
        result
    }
}
