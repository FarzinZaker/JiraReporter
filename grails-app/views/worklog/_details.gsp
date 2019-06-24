
<g:each in="${worklogs?.sort{-it.created.time}}" var="worklog">
    <g:render template="worklog" model="${[worklog: worklog]}"/>
</g:each>