package jirareporter

import com.mashape.unirest.http.HttpResponse
import com.mashape.unirest.http.Unirest
import grails.converters.JSON
import grails.gorm.transactions.Transactional
import grails.util.Holders
import org.codehaus.jettison.json.JSONArray
import org.codehaus.jettison.json.JSONObject
import sun.security.krb5.Config

import java.nio.channels.Channels
import java.nio.channels.ReadableByteChannel
import java.text.SimpleDateFormat

@Transactional
class AtHandService {

    def worklogService
    def componentService
    def clientService

    void downloadStructure() {

        def jiraClient = new JiraRestClient(new URI('https://psjira-us.aurea.com/'), JiraRestClient.getClient('farzin.zaker@aclate.com', 'farzin'))

        def fieldDefinitions = jiraClient.getURLAsList('https://psjira-us.aurea.com/rest/api/latest/field/').myArrayList
        println fieldDefinitions

        String worklogQyery = "order by created"


        def startAt = 0
        def maxResults = 1000
        while (true) {
            def result = jiraClient.getURL("https://psjira-us.aurea.com/rest/api/latest/search?jql=${URLEncoder.encode(worklogQyery, 'UTF-8')}&startAt=$startAt&maxResults=$maxResults&expand=renderedFields")

            if (result.total == 0 || !result.issues?.length())
                return

            println result.issues

            def fields = [:]
            def priorities = []
            def issueTypes = []
            def statuses = [:]
            result.issues.myArrayList.each { issue ->
                def currentFields = issue.myHashMap.fields.myHashMap
                currentFields.each { field ->
                    if (field.value)
                        if (field.value instanceof JSONObject && field.value.class != JSONObject.Null
                                || (field.value instanceof JSONArray && field.value.myArrayList.size() > 0)
                                || (field.value instanceof String && field.value?.trim() != '')) {
                            def name = fieldDefinitions.find { it.id == field.key }?.name ?: field.key
                            if (!fields.containsKey(name))
                                fields.put(name, field.value)
                            switch (name) {
                                case 'Priority':
                                    if (!priorities.contains(field.value.name))
                                        priorities << field.value.name
                                    break
                                case 'Status':

                                    def issueType = currentFields['issuetype'].name
                                    if (!statuses.containsKey(issueType))
                                        statuses.put(issueType, [])
                                    if (!statuses[issueType].contains(field.value.name))
                                        statuses[issueType] << field.value.name
                                    break
                                case 'Issue Type':
                                    if (!issueTypes.contains(field.value.name))
                                        issueTypes << field.value.name
                                    break
                            }
                        }
                }

                parseIssue(issue)
            }
            fields

            println fields
            println issueTypes
            println statuses
            println priorities

            if (result.issues?.length() < maxResults)
                return

            startAt += maxResults
        }
    }

    def downloadIssues() {

        def jiraClient = new JiraRestClient(new URI('https://psjira-us.aurea.com/'), JiraRestClient.getClient('farzin.zaker@aclate.com', 'farzin'))

        String worklogQyery = "order by created"
//        String worklogQyery = "key in (HPFM-31, HPFM-32)"

        def startAt = 0
        def maxResults = 1000
        def list = []
        while (true) {
            def result = jiraClient.getURL("https://psjira-us.aurea.com/rest/api/latest/search?jql=${URLEncoder.encode(worklogQyery, 'UTF-8')}&startAt=$startAt&maxResults=$maxResults&expand=renderedFields&fields=*all")

            if (result.total == 0)
                return

            def total = result.total
            def indexer = 1
            result.issues.myArrayList.each { json ->
                def issue = parseIssue(json)
                list << [
                        issue      : issue,
                        worklogs   : getWorklogs(JSONUtil.safeRead(json, 'fields.worklog'), issue),
                        comments   : getComments(JSONUtil.safeRead(json, 'fields.comment')),
                        attachments: getAttachments(JSONUtil.safeRead(json, 'fields.attachment'))
                ]

                println "${indexer++} / $total"
            }

            if (result.issues?.length() < maxResults)
                break

            startAt += maxResults
        }
        def total = list.size()
        def indexer = 1
        list.findAll { !it.issue.parent }.each { record ->
            if (indexer > 663)
                record.newKey = migrate(record.issue, null, record.attachments, record.comments, record.worklogs)
            println "${indexer++} / $total"
        }

        list.findAll { it.issue.parent }.each { record ->
            if (indexer > 663) {
                def parent = findParentKey(record.issue.parent.key)
                record.newKey = migrate(record.issue, parent, record.attachments, record.comments, record.worklogs)
            }
            println "${indexer++} / $total"
        }
    }

    String findParentKey(String originalKey) {
        def jiraClient = new JiraRestClient(new URI(Configuration.serverURL), JiraRestClient.getClient(Configuration.username, Configuration.password))

        String worklogQyery = "labels = ${originalKey}"

        def startAt = 0
        def maxResults = 1000

        def result = jiraClient.getURL("${Configuration.serverURL}/rest/api/latest/search?jql=${URLEncoder.encode(worklogQyery, 'UTF-8')}&startAt=$startAt&maxResults=$maxResults&expand=renderedFields&fields=*all")

        if (result.total == 0)
            throw new Exception('Parent Issue Not Found: ' + originalKey)

        if (result.total > 1)
            throw new Exception('More than ONE Parent Issue Found: ' + originalKey)

        def parentKey = getIssueKey(result.issues.myArrayList.find() as JSONObject)
        println "Parent: ${parentKey}"
        parentKey
    }

    String migrate(Issue issue, String parentKey, List<Map> attachments, List<Map> comments, List<Map> worklogs) {

        def key = create(issue, issue.clients?.find(), issue.components, issue.labels?.collect { it.name }, parentKey)
        uploadAttachments(attachments, key)
        addComments(comments, key)
        addWorklogs(worklogs, key)
        key
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
            finalData.put('assignee', [name: 'jplanner'])

        finalData.put('components', componentService.updateData(components))
        finalData.put('customfield_26105', clientService.updateData(client))
        if (labels?.size())
            finalData.put('labels', labels)

        if (parent) {
            finalData.put('parent', [key: parent])
        }


        finalData = [fields: finalData]
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

        return key
    }

    def uploadAttachments(List<Map> attachments, String issueKey) {
        attachments.each { attachment ->
            try {
                def localFile = new File(attachment.filename)
                if (localFile.exists())
                    localFile.delete()

                ByteArrayInputStream file = Unirest.get(attachment.content?.toString())
                        .basicAuth('farzin.zaker@aclate.com', 'farzin')
                        .asBinary()
                        .getBody();

                localFile.createNewFile()
                OutputStream os = new FileOutputStream(localFile);
                os.write(file.bytes);
                os.close();

                Unirest.setTimeouts(0, 0);
                HttpResponse<String> response = Unirest.post("${Configuration.serverURL}/rest/api/latest/issue/${issueKey}/attachments?notifyUsers=${false}")
                        .header("X-Atlassian-Token", "no-check")
                        .basicAuth(Configuration.username, Configuration.password)
                        .field("file", localFile)
                        .asString();
            } catch (Exception ex) {
                println ex.message
                throw ex
            }
            println "Attached: ${attachment.filename}"
        }

    }

    def addComments(List<Map> comments, String issueKey) {
        comments.sort { it.date }.each { comment ->
            def finalData = [body: """
*${comment.author}*:
${new SimpleDateFormat('MMM dd, yyyy - HH:mm').format(comment.date)}

${comment.body}
"""]

            try {
                def jiraClient = new JiraRestClient(new URI(Configuration.serverURL), JiraRestClient.getClient(Configuration.username, Configuration.password))
                def result = jiraClient.postWithResult("${Configuration.serverURL}/rest/api/latest/issue/${issueKey}/comment?notifyUsers=${false}", finalData)
//                println result
            } catch (Exception ex) {
                println ex.message
//                throw ex
            }
            println "Comment Added"
        }
    }

    def addWorklogs(List<Map> worklogs, String issueKey) {
        worklogs.sort { it.date }.each { worklog ->
            def finalData = [comment: """
*${worklog.author}*

${worklog.comment}
""", started: new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.000+0000").format(worklog.date), timeSpentSeconds: worklog.timeSpentSeconds]

            try {
                def jiraClient = new JiraRestClient(new URI(Configuration.serverURL), JiraRestClient.getClient(Configuration.username, Configuration.password))
                def result = jiraClient.postWithResult("${Configuration.serverURL}/rest/api/latest/issue/${issueKey}/worklog?notifyUsers=${false}", finalData)
//                println result
            } catch (Exception ex) {
                println ex.message
//                throw ex
            }
            println "Worklog Added"
        }
    }

    Issue parseIssue(def json) {
        def fields = json.myHashMap.fields.myHashMap
        def issue = new Issue()
        issue.key = getIssueKey(json)
        issue.issueType = getIssueType(json)
        issue.priority = getPriority(json)
        issue.project = getProject(json)
        issue.clients = getClients(json)
        issue.components = getComponents(json)
        issue.labels = getLabels(json)
        issue.description = getDescription(json)
        issue.summary = getSummary(json)
        issue.startDate = getStartDate(json)
        issue.dueDate = getDueDate(json)

        issue.parent = getParent(json)

        issue
    }

    IssueType getIssueType(JSONObject json) {
        def code = ''
        switch (json.fields.issuetype.name) {
            case 'Epic':
                code = 'Story'
                break
            case 'Task':
                code = 'Task'
                break
            case 'Story':
                code = 'Story'
                break
            case 'Improvement':
                code = 'Enhancement'
                break
            case 'New Feature':
                code = 'Feature Story'
                break
            case 'Sub-task':
                def parent = getParent(json)
                code = parent ? 'Sub-task' : 'Task'
                break
            case 'Epic':
                code = 'Development Task'
                break
            case 'Change Request':
                code = 'Story'
                break
            case 'Project Management':
                code = 'Task'
                break
            case 'Bug':
                code = 'Bugfix'
                break
        }
        IssueType.findByName(code)
    }

    Priority getPriority(JSONObject json) {
        def code = ''
        switch (json.fields.priority.name) {
            case 'Medium':
                code = 'Low'
                break
            case 'High':
                code = 'Medium'
                break
            case 'Low':
                code = 'Minor'
                break
            case 'Critical':
                code = 'High'
                break
            case 'Blocker':
                code = 'Showstopper'
                break
        }
        Priority.findByName(code)
    }

    Project getProject(JSONObject json) {
        Project.findByKey('PLHAN')
    }

    List<Client> getClients(JSONObject json) {
        [new Client(name: json.fields.project.name)]
    }

    List<Component> getComponents(JSONObject json) {
        [Component.findByName('@Hand')]
    }

    List<Label> getLabels(JSONObject json) {
        (['Legacy', getIssueKey(json), json.fields.status.name?.replace(' ', '_'), JSONUtil.safeRead(json, 'fields.assignee.name')] + json.fields.labels.myArrayList.collect { it?.toString() }).findAll { it }.collect { new Label(name: it) }
    }

    String getDescription(JSONObject json) {
        def created = parseDate(JSONUtil.safeRead(json, 'fields.created'))
        def updated = parseDate(JSONUtil.safeRead(json, 'fields.updated'))
        def formatter = new SimpleDateFormat('MMM dd, yyyy - HH:mm')
        [
                'Issue Key'                : getIssueKey(json),
//                Rank                       : JSONUtil.safeRead(json, 'fields.customfield_10449'),
                Creator                    : JSONUtil.safeRead(json, 'fields.creator.displayName'),
                Reporter                   : JSONUtil.safeRead(json, 'fields.reporter.displayName'),
                'Reported By'              : JSONUtil.safeRead(json, 'fields.customfield_11948'),
                Owner                      : JSONUtil.safeRead(json, 'fields.customfield_10099'),
                Created                    : created ? formatter.format(created) : null,
                Updated                    : updated ? formatter.format(updated) : null,
                Assignee                   : JSONUtil.safeRead(json, 'fields.assignee.displayName'),
                'Planned Start'            : JSONUtil.safeRead(json, 'fields.customfield_10645'),
                'Reopen Reason'            : JSONUtil.safeRead(json, 'fields.customfield_10641'),
                Resolution                 : JSONUtil.safeRead(json, 'fields.resolution.name'),
                'PSA Project URL'          : JSONUtil.safeRead(json, 'fields.customfield_11949'),
                'Update Jira URL'          : JSONUtil.safeRead(json, 'fields.customfield_11943'),
                Effort                     : JSONUtil.safeRead(json, 'fields.customfield_10040'),
                'Consultant Forecasted ETA': JSONUtil.safeRead(json, 'fields.customfield_10941'),
                'Expected Result'          : JSONUtil.safeRead(json, 'fields.customfield_10643'),
                Description                : JSONUtil.safeRead(json, 'fields.description')
        ].findAll { it.value }.collect { "*${it.key}*:\r\n${it.value}\r\n" }.join('\r\n')
    }

    String getIssueKey(JSONObject json) {
        json.key
    }

    String getSummary(JSONObject json) {
        json.fields.summary
    }

    Date getStartDate(JSONObject json) {
        def dateStr = JSONUtil.safeRead(json, 'fields.customfield_10644')
        dateStr ? new SimpleDateFormat("yyyy-MM-dd").parse(dateStr) : null
    }

    Date getDueDate(JSONObject json) {
        def dateStr = JSONUtil.safeRead(json, 'fields.duedate')
        dateStr ? new SimpleDateFormat("yyyy-MM-dd").parse(dateStr) : null
    }

    Issue getParent(JSONObject json) {
        def key = JSONUtil.safeRead(json, 'fields.parent.key')
        key ? new Issue(key: key) : null
    }

    List<Map> getWorklogs(JSONObject json, Issue issue) {

        def list = json.getJSONArray('worklogs')
        list.myArrayList.collect { obj ->
            def date = JSONUtil.safeRead(obj, 'started')
            [
                    author          : JSONUtil.safeRead(obj, 'author.displayName'),
                    comment         : JSONUtil.safeRead(obj, 'comment'),
                    date            : date ? parseDate(date) : null,
                    timeSpent       : JSONUtil.safeRead(obj, 'timeSpent'),
                    timeSpentSeconds: JSONUtil.safeRead(obj, 'timeSpentSeconds'),
            ]
        }
    }

    List<Map> getComments(JSONObject json) {
        def list = json.getJSONArray('comments')
        list.myArrayList.collect {
            [
                    author: JSONUtil.safeRead(it, 'author.displayName'),
                    date  : parseDate(JSONUtil.safeRead(it, 'updated')),
                    body  : JSONUtil.safeRead(it, 'body')
            ]
        }
    }

    List<Map> getAttachments(JSONArray json) {
        json.myArrayList.collect {
            [
                    author  : JSONUtil.safeRead(it, 'author.displayName'),
                    date    : parseDate(JSONUtil.safeRead(it, 'created')),
                    size    : JSONUtil.safeRead(it, 'size'),
                    mimeType: JSONUtil.safeRead(it, 'mimeType'),
                    content : JSONUtil.safeRead(it, 'content'),
                    filename: JSONUtil.safeRead(it, 'filename')
            ]
        }
    }

    Date parseDate(String date) {
        Date.parse("yyyy-MM-dd'T'HH:mm:ss", date.split('\\.').find())
    }
}
