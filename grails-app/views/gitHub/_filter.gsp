<form id="filterForm" action="${createLink(controller: 'gitHub', action: 'report')}" class="filterPanel">
    <g:render template="/filter/load"/>
    <g:render template="/filter/company"/>
    <g:render template="/filter/product"/>
    <g:render template="/filter/activeSince"/>
    <g:submitButton name="submit" value="FILTER" class="k-button k-primary"/>
    <g:render template="/filter/save"/>
</form>