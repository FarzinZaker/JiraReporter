<a href="https://jira.devfactory.com/browse/${issue.key}" target="_blank">
    <img src="${issue.issueType.icon}" alt="${issue.issueType.name}" title="${issue.issueType.name}"/> ${issue.key}
</a> - ${issue.summary}
<hr/>
Status: <span class="tag">
    <g:if test="${issue.status?.icon != 'https://jira.devfactory.com/'}">
        <img src="${issue.status?.icon}" alt="${issue.status?.name}" title="${issue.status?.name}"/>
    </g:if>
    ${issue.status?.name}</span>
Client: <span class="tag">${issue.clients?.join(' - ')}</span>