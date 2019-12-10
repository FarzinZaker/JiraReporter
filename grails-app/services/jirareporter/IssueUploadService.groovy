package jirareporter

import grails.converters.JSON
import grails.gorm.transactions.Transactional
import grails.util.Holders

@Transactional
class IssueUploadService {

    def enqueue(Issue issue, String source) {
        def list = []

        issue.dirtyPropertyNames.each { property ->
            if (!IssueUploadItem.findByIssueAndProperty(issue, property)) {
                list << property
                def issueSyncItem = new IssueUploadItem(issue: issue, property: property, value: JiraIssueMapper.formatType(property, issue."${property}"), source: source)
                if (!issueSyncItem.save())
                    throw new Exception("Unable to save sync item: ${issueSyncItem.errorMessage}")
            }
        }

        println issue.dirtyPropertyNames

        issue.discard()
    }


    String update(Issue issue) {
        def list = IssueUploadItem.findAllByIssueAndRetryCountLessThan(issue, 20).sort { it.dateCreated }

        def finalData = [:]
        list.each { issueUploadItem ->
            def data = [:]
            def fieldName = issueUploadItem.property
            if (!JiraIssueMapper.fieldsMap.containsKey(fieldName) || !JiraIssueMapper.fieldsMap[fieldName].containsKey('field'))
                return "$fieldName is not Mapped"

            if (JiraIssueMapper.fieldsMap[fieldName]['parser']) {
                def d = Holders.grailsApplication.mainContext.getBean(JiraIssueMapper.fieldsMap[fieldName].parser).updateData(issue)
                d.each { item ->
//                    if (!data.containsKey(item.key))
                    data.put(item.key, item.value)
//                    else
//                        item.value.each {
//                            data[item.key].put(it.key, it.value)
//                        }
                }
            } else {
                def path = JiraIssueMapper.fieldsMap[fieldName]['field'].split('\\.')
                for (def i = path.size() - 1; i >= 0; i--) {
                    if (i == path.size() - 1)
                        data.put(path[i], issueUploadItem.value)
                    else {
                        def newData = [:]
                        newData.put(path[i], data)
                        data = newData
                    }
                }
            }
            println issueUploadItem.property
            data.each { item ->
//                if (!finalData.containsKey(item.key))
                finalData.put(item.key, item.value)
//                else
//                    item.value.each {
//                        finalData[item.key].put(it.key, it.value)
//                    }
            }
        }

        println(finalData as JSON)
        try {
            def jiraClient = new JiraRestClient(new URI(Configuration.serverURL), JiraRestClient.getClient(Configuration.username, Configuration.password))
            jiraClient.put("${Configuration.serverURL}/rest/api/latest/issue/${issue.key}", [fields: finalData])

            new IssueDownloadItem(issue: issue, source: 'Issue Updated').save(flush: true)

//            IssueUploadItem.findAllByIssue(issue).each {
//                try {
//                    it.delete(flush: true)
//                } catch (ex) {
//                    println ex
//                    println it
//                }
//            }
            println IssueUploadItem.executeUpdate("delete IssueUploadItem where issue = :issue", [issue: issue])
        } catch (Exception ex) {
            list.each {
                it.errorMessage = ex.message
                it.lastTry = new Date()
                it.retryCount++
                it.save(flush: true)
            }
            println ex.message
//            throw ex
        }
    }

    String update(String issueKey, String fieldName, value) {
        def data = [:]
        if (!JiraIssueMapper.fieldsMap.containsKey(fieldName) || !JiraIssueMapper.fieldsMap[fieldName].containsKey('field'))
            return "$fieldName is not Mapped"

        def path = JiraIssueMapper.fieldsMap[fieldName]['field'].split('\\.')
        for (def i = path.size() - 1; i >= 0; i--) {
            if (i == path.size() - 1)
                data.put(path[i], JiraIssueMapper.formatType(fieldName, value))
            else {
                def newData = [:]
                newData.put(path[i], data)
                data = newData
            }
        }

        try {
            def jiraClient = new JiraRestClient(new URI(Configuration.serverURL), JiraRestClient.getClient(Configuration.username, Configuration.password))
            jiraClient.put("${Configuration.serverURL}/rest/api/latest/issue/$issueKey", [fields: data])
        } catch (Exception ex) {
//            println ex.message
            throw ex
        }
    }
}
