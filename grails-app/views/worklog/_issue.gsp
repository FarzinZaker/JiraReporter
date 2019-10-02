<div class="issueLink">
    <g:if test="${issue.parent}">
        <a href="https://jira.devfactory.com/browse/${issue.parent.key}" target="_blank">
            <img src="${issue.parent.issueType.icon}" alt="${issue.parent.issueType.name}" title="${issue.parent.issueType.name}"/> ${issue.parent.key}
        </a> - ${issue.parent.summary} /
    </g:if>
    <a href="https://jira.devfactory.com/browse/${issue.key}" target="_blank">
        <img src="${issue.issueType.icon}" alt="${issue.issueType.name}" title="${issue.issueType.name}"/> ${issue.key}
    </a> - ${issue.summary}
</div>

<div class="issueDetails">
    Status: <span class="tag">
    <g:if test="${issue.status?.icon != 'https://jira.devfactory.com/'}">
        <img src="${issue.status?.icon}" alt="${issue.status?.name}" title="${issue.status?.name}"/>
    </g:if>
    ${issue.status?.name}</span>
    Client: <span class="tag">${issue.clients?.join(' - ')}</span>
</div>