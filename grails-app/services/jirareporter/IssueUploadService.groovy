package jirareporter

import grails.converters.JSON
import grails.gorm.transactions.Transactional
import grails.util.Holders

@Transactional
class IssueUploadService {

    def springSecurityService
    def componentService
    def clientService
    def issueDownloadService
    def issueLinkTypeService

    def enqueue(Issue issue, String source, Date time = new Date(), Boolean save = false, String comment = null) {
        def list = []

        def user = springSecurityService.loggedIn ? User.findByUsername(springSecurityService.principal.username) : null
        if (!user?.jiraUsername || !user?.jiraPassword)
            user = null

        issue.dirtyPropertyNames.each { property ->
            if (!IssueUploadItem.findByIssueKeyAndPropertyAndRetryCountLessThan(issue.key, property, 20)) {
                list << property
                def saved = false
                while (!saved) {
                    try {
                        def issueSyncItem = new IssueUploadItem(issueKey: issue.key, property: property, value: JiraIssueMapper.formatType(property, issue."${property}"), source: source, comment: comment, creator: user, time: time)
                        if (!issueSyncItem.save(flush: true))
                            throw new Exception("Unable to save sync item: ${issueSyncItem.errorMessage}")
                        saved = true
                    } catch (ex) {
                        println ex.message
                        Thread.sleep(2000)
                    }
                }
            }
        }

//        println issue.dirtyPropertyNames

        if (!save)
            issue.discard()
        else if (!issue.save(flush: true))
            throw new Exception("Unable to persist Issue changes.")
//        else
//            println 'DONE'
    }

    String update(String issueKey, Date time, User creator = null) {
        def list = IssueUploadItem.findAllByIssueKeyAndTimeAndRetryCountLessThan(issueKey, time, 20).sort {
            it.time
        }
        if (!list?.size())
            return

        def issue = Issue.findByKey(issueKey)

        def comments = list.collect { it.comment }.findAll { it }.unique { it }
        def comment = comments?.size() ? comments.join('\\\\') : null

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
                    if (i == path.size() - 1) {
                        if (JiraIssueMapper.fieldsMap[fieldName]['type'] == Double)
                            data.put(path[i], Double.parseDouble(issueUploadItem.value))
                        else
                            data.put(path[i], issueUploadItem.value)
                    } else {
                        def newData = [:]
                        newData.put(path[i], data)
                        data = newData
                    }
                }
            }
//            println issueUploadItem.property
            data.each { item ->
//                if (!finalData.containsKey(item.key))
                finalData.put(item.key, item.value)
//                else
//                    item.value.each {
//                        finalData[item.key].put(it.key, it.value)
//                    }
            }
        }

//        println(finalData as JSON)
        finalData = [fields: finalData]
        if (comment)
            finalData.put('update', [comment: [[add: [body: comment]]]])
        try {
            def notifyUsers = creator ? true : false
            def jiraClient = new JiraRestClient(new URI(Configuration.serverURL), JiraRestClient.getClient(creator?.jiraUsername ?: Configuration.username, creator?.jiraPassword ? AESCryption.decrypt(creator.jiraPassword) : Configuration.password))
            jiraClient.put("${Configuration.serverURL}/rest/api/latest/issue/${issue.key}?notifyUsers=${notifyUsers}", finalData)

            def saved = false
            while (!saved) {
                try {
                    issueDownloadService.enqueue(issue.key, 'Issue Updated')
                    saved = true
                } catch (Exception ignore) {
                    println "retrying to queue issue for download"
                }
            }

            IssueUploadItem.findAllByIssueKeyAndTime(issueKey, time).each {
                try {
                    it.delete(flush: true)
                } catch (ex) {
                    println ex
                    println it
                }
            }
//            println IssueUploadItem.executeUpdate("delete IssueUploadItem where issue = :issue", [issue: issue])
        } catch (Exception ex) {
            list.each {
                it.errorMessage = ex.message
                it.lastTry = new Date()
                it.retryCount++
                it.save(flush: true)
            }
//            println ex.message
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
            println ex.message
            throw ex
        }
    }

    String create(Issue issue, Client client, Set<Component> components, List<String> labels = [], String parent = null, User creator = null, Boolean download = false) {
        def list = [:]
        issue.properties.each { property ->
            if (!property.key.endsWith('Id') && property.value)
                list.put(property.key, property.value)
        }

        if (!list?.size())
            return

        def finalData = [:]
        list.each { field ->
            def data = [:]
            def fieldName = field.key
            if (!JiraIssueMapper.fieldsMap.containsKey(fieldName) || !JiraIssueMapper.fieldsMap[fieldName].containsKey('field'))
                return "$fieldName is not Mapped"

            if (JiraIssueMapper.fieldsMap[fieldName]['parser']) {
                def d = Holders.grailsApplication.mainContext.getBean(JiraIssueMapper.fieldsMap[fieldName].parser).updateData(issue)
                d.each { item ->
                    data.put(item.key, item.value)
                }
            } else {
                def path = JiraIssueMapper.fieldsMap[fieldName]['field'].split('\\.')
                for (def i = path.size() - 1; i >= 0; i--) {
                    if (i == path.size() - 1)
                        data.put(path[i], field.value)
                    else {
                        def newData = [:]
                        newData.put(path[i], data)
                        data = newData
                    }
                }
            }
            data.each { item ->
                finalData.put(item.key, item.value)
            }
        }

        if (!issue.assignee)
            finalData.put('assignee', [name: ''])

        finalData.put('components', componentService.updateData(components.findAll { it.project?.id == issue?.project?.id }))
        finalData.put('customfield_26105', clientService.updateData(client))
        if (labels?.size())
            finalData.put('labels', labels)

        def parentLinkedIssue = null
        if (parent) {
            def parentIssue = Issue.findByKey(parent)
            if (!parentIssue || (!parentIssue.issueType.subtask && issue.issueType.subtask))
                finalData.put('parent', [key: parent])
            else parentLinkedIssue = parentIssue
        }


        finalData = [fields: finalData]
//        println(finalData as JSON)
        def key
        try {
            def notifyUsers = creator ? true : false
            def jiraClient = new JiraRestClient(new URI(Configuration.serverURL), JiraRestClient.getClient(creator?.jiraUsername ?: Configuration.username, creator?.jiraPassword ? AESCryption.decrypt(creator.jiraPassword) : Configuration.password))
            def result = jiraClient.postWithResult("${Configuration.serverURL}/rest/api/latest/issue/?notifyUsers=${notifyUsers}", finalData)
            key = result.key
        } catch (Exception ex) {
            println ex.message
            throw ex
        }
        println "Issue Created: $key"
        def saved = false
        def retries = 0
        if (parentLinkedIssue) {
            def data = [
                    type        : [
                            name: issueLinkTypeService.getIssueLinkTypeName('is child of')
                    ],
                    inwardIssue : [
                            key: key
                    ],
                    outwardIssue: [
                            key: parent
                    ]
            ]

            def jiraClient = new JiraRestClient(new URI(Configuration.serverURL), JiraRestClient.getClient(Configuration.username, Configuration.password))
            jiraClient.post("${Configuration.serverURL}/rest/api/latest/issueLink", data)

            println "Linked to parent Issue"


            saved = false
            retries = 0
            while (!saved && retries++ <= 5) {
                try {
                    issueDownloadService.download(parent)
                    println 'downloaded parent issue'
                    saved = true
                } catch (exception) {
                    throw exception
                }
            }
        }

        while (!saved && retries++ <= 5) {
            try {
                issueDownloadService.download(key)
                println 'downloaded issue'
                saved = true
            } catch (exception) {
                throw exception
            }
        }

        return key
    }
}
