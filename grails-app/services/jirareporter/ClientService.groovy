package jirareporter

import grails.gorm.transactions.Transactional
import org.codehaus.jettison.json.JSONObject

@Transactional
class ClientService {

    Client parse(String obj) {
        if (obj == JSONObject.NULL)
            return null

        def name = obj?.replace('"', '')
        def client = Client.findByName(name)
        if (!client) {
            client = new Client(name: name)
            client = client.save(flush: true)
            if (!client)
                throw new Exception("Error Saving Client")
        }
        client
    }
}
