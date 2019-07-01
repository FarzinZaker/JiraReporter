package jirareporter

import grails.gorm.transactions.Transactional
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClientBuilder

@Transactional
class CrossOverService {

    def getWorkingHours() {



        //login
        def get = new HttpGet('https://api.crossover.com/api/v2/timetracking/timesheets/assignment?date=2019-06-4&fullTeam=false&managerId=1721621&period=MONTH&teamId=3439')
        get.addHeader('Accept', 'application/json, text/plain, */*')
        get.addHeader('Authorization', "Basic ${"${username}:${password}".bytes.encodeBase64().toString()}")
        get.addHeader('Content-Type', 'application/json;charset=UTF-8')
        get.addHeader('Origin', 'https://app.crossover.com')
        get.addHeader('Referer', 'https://app.crossover.com/x/dashboard/team/3439/1721621/team-timesheet?date=2019-06-03')
        get.addHeader('User-Agent', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.100 Safari/537.36')

        def client = HttpClientBuilder.create().build()
        def response = client.execute(get)

        def bufferedReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()))
        render bufferedReader.getText()
    }
}
