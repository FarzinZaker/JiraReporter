package jirareporter


import grails.gorm.transactions.Transactional
import org.codehaus.jettison.json.JSONArray
import org.codehaus.jettison.json.JSONObject

@Transactional
class WorklogService {

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
                author          : [
                        url         : obj.author.self,
                        name        : obj.author.name,
                        key         : obj.author.key,
                        emailAddress: obj.author.emailAddress,
                        displayName : obj.author.displayName,
                        active      : obj.author.active,
                        timeZone    : obj.author.timeZone,
                        avatars     : obj.author.avatarUrls.myHashMap,
                ],
                updateAuthor    : [
                        url         : obj.updateAuthor.self,
                        name        : obj.updateAuthor.name,
                        key         : obj.updateAuthor.key,
                        emailAddress: obj.updateAuthor.emailAddress,
                        displayName : obj.updateAuthor.displayName,
                        active      : obj.updateAuthor.active,
                        timeZone    : obj.updateAuthor.timeZone,
                        avatars     : obj.updateAuthor.avatarUrls.myHashMap,
                ],
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
