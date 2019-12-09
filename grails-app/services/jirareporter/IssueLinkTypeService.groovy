package jirareporter

import grails.converters.JSON
import grails.gorm.transactions.Transactional
import org.grails.web.json.JSONArray

@Transactional
class IssueLinkTypeService {

    JSONArray linkTypes = null

    private void loadLinkTypes() {
        def jiraClient = new JiraRestClient(new URI(Configuration.serverURL), JiraRestClient.getClient(Configuration.username, Configuration.password))
        def result = jiraClient.getURL("${Configuration.serverURL}/rest/api/latest/issueLinkType")
        linkTypes = JSON.parse(result?.toString()).issueLinkTypes as JSONArray
    }

    String getIssueLinkTypeId(String name) {
        if (!linkTypes)
            loadLinkTypes()

        linkTypes.find { it.outward == name }.id
    }

    String getIssueLinkTypeName(String name) {
        if (!linkTypes)
            loadLinkTypes()

        linkTypes.find { it.outward == name }.name
    }
}
