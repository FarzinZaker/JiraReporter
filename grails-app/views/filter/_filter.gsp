<g:form controller="worklog" action="report" class="filterPanel">
    <div>
        <g:render template="/filter/time"/>
    </div>

    <div>
        <g:render template="/filter/project"/>
    </div>

    <div>
        <g:render template="/filter/issueType"/>
    </div>

    <div>
        <g:render template="/filter/user"/>
    </div>
    <g:submitButton name="submit" value="FILTER"/>
</g:form>