<%@ page import="grails.converters.JSON" %>

<div class="worklog">
    <div class="user">
        <g:render template="user" model="${[user: worklog.author]}"/>
        %{--<g:formatDate date="${worklog.created}"/>--}%
    </div>
    %{--<g:if test="${worklog.created != worklog.updated}">--}%
    %{--<div>--}%
    %{--<g:render template="user" model="${[user: worklog.updateAuthor]}"/> <label>Updated: </label> <g:formatDate date="${worklog.updated}"/>--}%
    %{--</div>--}%
    %{--</g:if>--}%

    <div class="timeSpent">
        ${worklog.timeSpent}
    </div>

    <div class="started">
        <g:formatDate date="${worklog.started}"/>
    </div>

    <div class="comment">
        ${worklog.comment}
    </div>

    <div class="issue">
        <g:render template="issue" model="${[key: worklog.task.id]}"/>
    </div>

    %{--<div>--}%
    %{--<g:formatDate date="${worklog.started}"/>--}%
    %{--</div>--}%



    %{--<div>--}%
    %{--${worklog.timeSpentSeconds}--}%
    %{--</div>--}%
</div>