<g:if test="${worklogs?.size()}">
    <g:each in="${worklogs?.sort { -it.created.time }}" var="worklog">
        <g:render template="worklog" model="${[worklog: worklog]}"/>
    </g:each>
</g:if>
<g:else>
    <div class="info" style="margin-top:20px;">
        No records to display
    </div>
</g:else>