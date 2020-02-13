package jirareporter

import grails.gorm.transactions.Transactional
import org.codehaus.jettison.json.JSONObject

@Transactional
class LabelService {

    Label parse(String obj) {
        if (obj == JSONObject.NULL)
            return null

        def name = obj?.replace('"', '')
        def label = Label.findByName(name)
        if (!label) {
            label = new Label(name: name)
            label = label.save(flush: true)
            if (!label)
                throw new Exception("Error Saving Label")
        }
        label
    }

    List<String> updateData(Label label) {
        [label.name]
    }
}
