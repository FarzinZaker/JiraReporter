package jirareporter

import grails.gorm.transactions.Transactional
import org.codehaus.jettison.json.JSONObject

@Transactional
class IssueProductService {

    IssueProduct parse(String obj) {
        if (obj == JSONObject.NULL)
            return null

        def name = obj?.replace('"', '')
        def product = IssueProduct.findByName(name)
        if (!product) {
            product = new IssueProduct(name: name)
            product = product.save(flush: true)
            if (!product)
                throw new Exception("Error Saving Product")
        }
        product
    }

    List<String> updateData(IssueProduct product) {
        [product.name]
    }
}
