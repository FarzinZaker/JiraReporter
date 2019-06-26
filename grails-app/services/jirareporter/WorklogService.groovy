package jirareporter


import grails.gorm.transactions.Transactional
import org.codehaus.jettison.json.JSONArray
import org.codehaus.jettison.json.JSONObject

@Transactional
class WorklogService {

    def userService

    List<Map> parseList(JSONArray list) {
        def worklogs = []
        for (def i = 0; i < list.length(); i++) {
            def obj = list.getJSONObject(i)
            worklogs << parse(obj)
        }
        worklogs
    }

    Map parse(JSONObject obj) {
        [
                url             : obj.self,
                author          : userService.parse(obj.author),
                updateAuthor    : userService.parse(obj.updateAuthor),
                comment         : obj.comment,
                created         : Date.parse("yyyy-MM-dd'T'hh:mm:ss.000+0000", obj.created),
                updated         : Date.parse("yyyy-MM-dd'T'hh:mm:ss.000+0000", obj.updated),
                started         : Date.parse("yyyy-MM-dd'T'hh:mm:ss.000+0000", obj.started),
                timeSpent       : obj.timeSpent,
                timeSpentSeconds: obj.timeSpentSeconds,
                id              : obj.id,
                issueId         : obj.issueId
        ]
    }
}
