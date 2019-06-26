package jirareporter

import grails.gorm.transactions.Transactional
import groovy.time.TimeCategory
import org.codehaus.jettison.json.JSONObject

@Transactional
class CacheService {

    private static final Map data = [:]

    def store(String key, Object value) {
        def expiry = new Date()
        use(TimeCategory) {
            expiry = expiry + 1.hour
        }
        data[key] = [expiry: expiry, value: value]
    }

    Boolean has(String key) {
        data.containsKey(key) && data[key].expiry > new Date()
    }

    Object retrieve(String key) {
        if (!has(key))
            return null

        data[key].value
    }
}
