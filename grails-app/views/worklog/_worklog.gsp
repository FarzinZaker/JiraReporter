<%@ page import="grails.converters.JSON" %>

<div class="worklog ${worklog.task.issueType.name.replace(' ', '_').replace('-', '')}">
    <div class="user">
        <g:render template="user" model="${[user: worklog.author]}"/>
    </div>

    <div class="timeSpent">
        ${worklog.timeSpent}
    </div>

    <div class="started">
        <g:formatDate date="${worklog.started}"/>
    </div>

    <div class="comment">
        <markdown:renderHtml>${worklog.comment ?: 'No Description'}</markdown:renderHtml>
    </div>

    <div class="issue">
        <g:render template="issue" model="${[issue: worklog.task]}"/>
    </div>
</div>