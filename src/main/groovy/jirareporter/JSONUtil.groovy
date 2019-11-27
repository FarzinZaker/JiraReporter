package jirareporter

import org.grails.web.json.JSONObject

class JSONUtil {

    static def safeRead(obj, property) {
        try {
            def value = obj
            def parts = property.split('\\.')
            parts.each { part ->
                value = value."${part}"
            }
            value == 'null' || value?.toString()?.toLowerCase() == 'null' ? null : value
        } catch (Exception ex) {
//            println ex.message
            return null
        }
    }
}
