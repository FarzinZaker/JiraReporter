<g:if test="${accomplishments?.size()}">
    <g:render template="accomplishmentsToolbar"/>
    <g:each in="${accomplishments}" var="task">

        <div class="task ${task.issueType.name.replace(' ', '_').replace('-', '')}">
            <div class="issue">
                <div>
                    <g:if test="${task.parent}">
                        <a href="https://jira.devfactory.com/browse/${task.parent.key}" target="_blank">
                            <img src="${task.parent.issueType.icon}" alt="${task.parent.issueType.name}"
                                 title="${task.parent.issueType.name}"/> ${task.parent.key}
                        </a> - ${task.parent.summary} /
                    </g:if>
                    <a href="https://jira.devfactory.com/browse/${task.key}" target="_blank">
                        <img src="${task.issueType.icon}" alt="${task.issueType.name}"
                             title="${task.issueType.name}"/> ${task.key}
                    </a> - ${task.summary}
                </div>
            </div>

            <g:each in="${task.assignees.keySet()}" var="author">
                <div class="user">
                    <g:render template="user" model="${[user: author]}"/>
                </div>

                <div class="worklogs">
                    <g:each in="${task.assignees[author]}" var="worklog">
                        <div class="timeSpent">
                            ${worklog.timeSpent}
                        </div>

                        <div class="started">
                            <g:formatDate date="${worklog.started}"/>
                        </div>

                        <div class="comment">
                            <markdown:renderHtml>${worklog.comment ?: 'No Description'}</markdown:renderHtml>
                        </div>
                    </g:each>
                </div>
            </g:each>

            <div class="issueDetails">
                Status: <span class="tag">
                <g:if test="${task.status?.icon != 'https://jira.devfactory.com/'}">
                    <img src="${task.status?.icon}" alt="${task.status?.name}" title="${task.status?.name}"/>
                </g:if>
                ${task.status?.name}</span>
                Client: <span class="tag">${task.clients?.join(' - ')}</span>
                Components: <span class="tag">${task.components?.name?.join(' - ')}</span>
            </div>
        </div>
    </g:each>
</g:if>
<g:else>
    <div class="info" style="margin-top:20px;">
        No records to display
    </div>
</g:else>
