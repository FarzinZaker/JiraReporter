<form id="filterForm" action="${createLink(controller: 'worklog', action: 'report')}" class="filterPanel">
    <g:render template="/filter/load"/>
    <g:render template="/filter/time"/>
    <g:render template="/filter/project"/>
    <g:render template="/filter/issueType"/>
    <g:render template="/filter/priority"/>
    <g:render template="/filter/status"/>
    <g:render template="/filter/component"/>
    <g:render template="/filter/client"/>
    <g:render template="/filter/label"/>
    <g:render template="/filter/team"/>
    <g:render template="/filter/user"/>
    <g:render template="/filter/billable"/>
    <g:render template="/filter/noRecurring"/>
    <g:submitButton name="submit" value="FILTER" class="k-button k-primary"/>
    <g:render template="/filter/save"/>
</form>