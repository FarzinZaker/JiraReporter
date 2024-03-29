<form id="filterForm" class="filterPanel">
    <g:render template="/filter/load"/>
    <g:render template="/filter/time"/>
    <g:render template="/filter/issue"/>
    <g:render template="/filter/issueProduct"/>
    <g:render template="/filter/project"/>
    <g:render template="/filter/issueType"/>
    <g:render template="/filter/priority"/>
    <g:render template="/filter/status"/>
    <g:render template="/filter/component"/>
    <g:render template="/filter/client"/>
    <g:render template="/filter/label"/>
    <g:render template="/filter/team"/>
    <g:render template="/filter/user"/>
    <g:render template="/filter/noRecurring"/>
    <g:render template="/filter/unassigned"/>
    <input type="button" onclick="reloadPlanner()" value="FILTER" class="k-button k-primary"/>
    <g:render template="/filter/save"/>
</form>
<script language="JavaScript" type="text/javascript">
    function reloadPlanner(notToggleFilterPanel) {
        $('#toolbar').slideUp();
        if (!notToggleFilterPanel)
            toggleFilterPanel();
        gantt.clearAll();
        gantt.load('${createLink(controller: 'planner', action: 'issues')}?' + $('#filterForm').serialize(), function () {
            resourcesStore.parse(filterResources());
            gantt.refreshData();
            gantt.sort('start_date', false);
            $('#toolbar').slideDown();
            showToday();
            resizeGantt();
        });
    }
</script>