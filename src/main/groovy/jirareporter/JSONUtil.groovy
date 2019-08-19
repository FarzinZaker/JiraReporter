package jirareporter

class JSONUtil {

    static def safeRead(obj, property) {
        try {
            def value = obj
            def parts = property.split('\\.')
            parts.each { part ->
                value = value."${part}"
            }
            value
        } catch (Exception ex) {
//            println ex.message
            return null
        }
    }
}
